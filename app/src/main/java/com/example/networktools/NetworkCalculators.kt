package com.example.networktools

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

object SubnetCalculator {

    data class SubnetResult(
        val ip: String,
        val prefix: Int,
        val networkAddress: String,
        val broadcastAddress: String,
        val subnetMask: String,
        val wildcardMask: String,
        val firstUsableHost: String,
        val lastUsableHost: String,
        val totalAddresses: Long,
        val usableHosts: Long,
        val binaryIp: String,
        val binaryMask: String
    )

    fun calculate(ipInput: String, prefixInput: Int): Result<SubnetResult> {
        val ipLong = parseIpToLong(ipInput.trim()) ?: return Result.failure(IllegalArgumentException("Invalid IPv4 Address format"))
        if (prefixInput !in 0..32) {
            return Result.failure(IllegalArgumentException("Prefix must be between 0 and 32"))
        }

        val maskLong = if (prefixInput == 0) 0L else (0xFFFFFFFFL shl (32 - prefixInput)) and 0xFFFFFFFFL
        val wildcardLong = maskLong.inv() and 0xFFFFFFFFL
        val networkLong = ipLong and maskLong
        val broadcastLong = networkLong or wildcardLong

        val totalAddresses = (1L shl (32 - prefixInput))
        val (firstUsable, lastUsable, usableHosts) = when (prefixInput) {
            32 -> Triple(longToIp(networkLong), longToIp(networkLong), 1L)
            31 -> Triple(longToIp(networkLong), longToIp(broadcastLong), 2L) // RFC 3021 point-to-point
            0 -> Triple(longToIp(networkLong + 1), longToIp(broadcastLong - 1), (1L shl 32) - 2)
            else -> Triple(longToIp(networkLong + 1), longToIp(broadcastLong - 1), (totalAddresses - 2).coerceAtLeast(0L))
        }

        return Result.success(
            SubnetResult(
                ip = longToIp(ipLong),
                prefix = prefixInput,
                networkAddress = longToIp(networkLong),
                broadcastAddress = longToIp(broadcastLong),
                subnetMask = longToIp(maskLong),
                wildcardMask = longToIp(wildcardLong),
                firstUsableHost = firstUsable,
                lastUsableHost = lastUsable,
                totalAddresses = totalAddresses,
                usableHosts = usableHosts,
                binaryIp = toBinaryString(ipLong),
                binaryMask = toBinaryString(maskLong)
            )
        )
    }

    fun parseIpToLong(ip: String): Long? {
        val parts = ip.split(".")
        if (parts.size != 4) return null
        var result = 0L
        for (part in parts) {
            val octet = part.toLongOrNull() ?: return null
            if (octet !in 0..255) return null
            result = (result shl 8) or octet
        }
        return result
    }

    fun longToIp(ipLong: Long): String {
        return "${(ipLong shr 24) and 0xFF}.${(ipLong shr 16) and 0xFF}.${(ipLong shr 8) and 0xFF}.${ipLong and 0xFF}"
    }

    fun toBinaryString(value: Long): String {
        val o1 = ((value shr 24) and 0xFF).toString(2).padStart(8, '0')
        val o2 = ((value shr 16) and 0xFF).toString(2).padStart(8, '0')
        val o3 = ((value shr 8) and 0xFF).toString(2).padStart(8, '0')
        val o4 = (value and 0xFF).toString(2).padStart(8, '0')
        return "$o1.$o2.$o3.$o4"
    }

    fun maskToPrefix(mask: String): Int? {
        val maskLong = parseIpToLong(mask) ?: return null
        var prefix = 0
        var foundZero = false
        for (i in 31 downTo 0) {
            val bit = (maskLong shr i) and 1L
            if (bit == 1L) {
                if (foundZero) return null // Discontiguous mask
                prefix++
            } else {
                foundZero = true
            }
        }
        return prefix
    }
}

object VlsmCalculator {

    data class VlsmAllocation(
        val name: String,
        val requiredHosts: Int,
        val allocatedHosts: Long,
        val prefix: Int,
        val networkAddress: String,
        val subnetMask: String,
        val firstUsableHost: String,
        val lastUsableHost: String,
        val broadcastAddress: String
    )

    data class VlsmResult(
        val baseNetwork: String,
        val basePrefix: Int,
        val allocations: List<VlsmAllocation>,
        val remainingSpace: String,
        val totalRequired: Long,
        val totalCapacity: Long
    )

    fun calculate(
        baseIp: String,
        basePrefix: Int,
        hostRequirements: List<Pair<String, Int>> // Name to required hosts
    ): Result<VlsmResult> {
        val baseIpLong = SubnetCalculator.parseIpToLong(baseIp)
            ?: return Result.failure(IllegalArgumentException("Invalid Base IP"))
        if (basePrefix !in 0..30) {
            return Result.failure(IllegalArgumentException("Base prefix must be between 0 and 30"))
        }

        val baseMask = if (basePrefix == 0) 0L else (0xFFFFFFFFL shl (32 - basePrefix)) and 0xFFFFFFFFL
        var currentIp = baseIpLong and baseMask
        val totalBaseCapacity = 1L shl (32 - basePrefix)

        // Sort descending by host count for optimal VLSM packing
        val sortedReqs = hostRequirements.sortedByDescending { it.second }
        var totalAllocated = 0L
        val allocations = mutableListOf<VlsmAllocation>()

        for ((name, hosts) in sortedReqs) {
            if (hosts <= 0) continue
            // Bits needed for hosts + network + broadcast
            val neededBits = ceil(log2((hosts + 2).toDouble())).toInt().coerceAtLeast(2)
            val prefix = 32 - neededBits
            if (prefix < basePrefix) {
                return Result.failure(IllegalArgumentException("Requirement of $hosts hosts exceeds base network size /$basePrefix"))
            }

            val blockSize = 1L shl neededBits
            // Check boundary alignment
            if (currentIp % blockSize != 0L) {
                currentIp = ((currentIp / blockSize) + 1) * blockSize
            }

            if (totalAllocated + blockSize > totalBaseCapacity) {
                return Result.failure(IllegalArgumentException("Insufficient IP address space in /$basePrefix for all requirements"))
            }

            val maskLong = (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
            val broadcastLong = currentIp + blockSize - 1

            allocations.add(
                VlsmAllocation(
                    name = name,
                    requiredHosts = hosts,
                    allocatedHosts = blockSize - 2,
                    prefix = prefix,
                    networkAddress = SubnetCalculator.longToIp(currentIp),
                    subnetMask = SubnetCalculator.longToIp(maskLong),
                    firstUsableHost = SubnetCalculator.longToIp(currentIp + 1),
                    lastUsableHost = SubnetCalculator.longToIp(broadcastLong - 1),
                    broadcastAddress = SubnetCalculator.longToIp(broadcastLong)
                )
            )

            currentIp += blockSize
            totalAllocated += blockSize
        }

        val remainingHosts = totalBaseCapacity - totalAllocated
        val remainingDesc = if (remainingHosts > 0) {
            "${SubnetCalculator.longToIp(currentIp)} ($remainingHosts addresses free)"
        } else {
            "No address space remaining"
        }

        return Result.success(
            VlsmResult(
                baseNetwork = SubnetCalculator.longToIp(baseIpLong and baseMask),
                basePrefix = basePrefix,
                allocations = allocations,
                remainingSpace = remainingDesc,
                totalRequired = sortedReqs.sumOf { it.second.toLong() },
                totalCapacity = totalBaseCapacity
            )
        )
    }
}

object WildcardCalculator {
    fun maskToWildcard(mask: String): Result<String> {
        val maskLong = SubnetCalculator.parseIpToLong(mask)
            ?: return Result.failure(IllegalArgumentException("Invalid Subnet Mask"))
        val wildcardLong = maskLong.inv() and 0xFFFFFFFFL
        return Result.success(SubnetCalculator.longToIp(wildcardLong))
    }

    fun wildcardToMask(wildcard: String): Result<String> {
        val wildcardLong = SubnetCalculator.parseIpToLong(wildcard)
            ?: return Result.failure(IllegalArgumentException("Invalid Wildcard Mask"))
        val maskLong = wildcardLong.inv() and 0xFFFFFFFFL
        return Result.success(SubnetCalculator.longToIp(maskLong))
    }
}

object Ipv6Tools {
    data class Ipv6Analysis(
        val expanded: String,
        val compressed: String,
        val addressType: String,
        val scope: String
    )

    fun analyze(ipv6Input: String): Result<Ipv6Analysis> {
        val trimmed = ipv6Input.trim().lowercase()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("Empty IPv6 address"))

        // Expand
        val expanded = expand(trimmed) ?: return Result.failure(IllegalArgumentException("Invalid IPv6 Address"))
        val compressed = compress(expanded)
        val (type, scope) = detectType(expanded)

        return Result.success(
            Ipv6Analysis(
                expanded = expanded,
                compressed = compressed,
                addressType = type,
                scope = scope
            )
        )
    }

    fun expand(ip: String): String? {
        val clean = ip.trim().lowercase()
        val parts = clean.split("::")
        if (parts.size > 2) return null // Only one '::' allowed

        val leftTokens = if (parts[0].isNotEmpty()) parts[0].split(":") else emptyList()
        val rightTokens = if (parts.size == 2 && parts[1].isNotEmpty()) parts[1].split(":") else emptyList()

        if (parts.size == 1 && leftTokens.size != 8) return null
        val totalExplicit = leftTokens.size + rightTokens.size
        if (totalExplicit > 8) return null

        val zerosToInsert = 8 - totalExplicit
        val fullList = mutableListOf<String>()

        for (token in leftTokens) {
            val padded = token.padStart(4, '0')
            if (padded.length != 4 || padded.any { it !in "0123456789abcdef" }) return null
            fullList.add(padded)
        }
        repeat(zerosToInsert) {
            fullList.add("0000")
        }
        for (token in rightTokens) {
            val padded = token.padStart(4, '0')
            if (padded.length != 4 || padded.any { it !in "0123456789abcdef" }) return null
            fullList.add(padded)
        }

        if (fullList.size != 8) return null
        return fullList.joinToString(":")
    }

    fun compress(expanded: String): String {
        val parts = expanded.split(":").map { it.trimStart('0').ifEmpty { "0" } }
        // Find longest sequence of "0"
        var bestStart = -1
        var bestLen = 0
        var currentStart = -1
        var currentLen = 0

        for (i in parts.indices) {
            if (parts[i] == "0") {
                if (currentStart == -1) currentStart = i
                currentLen++
                if (currentLen > bestLen) {
                    bestLen = currentLen
                    bestStart = currentStart
                }
            } else {
                currentStart = -1
                currentLen = 0
            }
        }

        if (bestLen <= 1) {
            return parts.joinToString(":")
        }

        val left = parts.subList(0, bestStart).joinToString(":")
        val right = parts.subList(bestStart + bestLen, parts.size).joinToString(":")
        return "$left::$right"
    }

    fun detectType(expanded: String): Pair<String, String> {
        val firstHex = expanded.substring(0, 4)
        return when {
            expanded == "0000:0000:0000:0000:0000:0000:0000:0001" -> "Loopback (::1)" to "Node-Local"
            expanded == "0000:0000:0000:0000:0000:0000:0000:0000" -> "Unspecified (::)" to "N/A"
            firstHex.startsWith("fe8") || firstHex.startsWith("fe9") || firstHex.startsWith("fea") || firstHex.startsWith("feb") ->
                "Link-Local Unicast (fe80::/10)" to "Link-Local"
            firstHex.startsWith("fc") || firstHex.startsWith("fd") ->
                "Unique-Local Private (fc00::/7)" to "Site-Local / Private"
            firstHex.startsWith("ff") -> "Multicast (ff00::/8)" to "Multicast"
            firstHex.startsWith("2") || firstHex.startsWith("3") -> "Global Unicast (2000::/3)" to "Global Internet"
            else -> "Reserved / Other" to "Special"
        }
    }
}

object BandwidthCalculator {
    data class TransferResult(
        val totalSeconds: Double,
        val formattedTime: String,
        val bitsTransferred: Double
    )

    fun calculate(
        fileSizeValue: Double,
        fileSizeUnit: String, // B, KB, MB, GB, TB
        bandwidthValue: Double,
        bandwidthUnit: String, // bps, Kbps, Mbps, Gbps
        efficiencyPercent: Double = 90.0
    ): TransferResult {
        val bytes = when (fileSizeUnit) {
            "B" -> fileSizeValue
            "KB" -> fileSizeValue * 1024
            "MB" -> fileSizeValue * 1024 * 1024
            "GB" -> fileSizeValue * 1024 * 1024 * 1024
            "TB" -> fileSizeValue * 1024.0.pow(4)
            else -> fileSizeValue
        }
        val totalBits = bytes * 8.0

        val bps = when (bandwidthUnit) {
            "bps" -> bandwidthValue
            "Kbps" -> bandwidthValue * 1000
            "Mbps" -> bandwidthValue * 1_000_000
            "Gbps" -> bandwidthValue * 1_000_000_000
            else -> bandwidthValue
        }

        val effectiveBps = bps * (efficiencyPercent.coerceIn(10.0, 100.0) / 100.0)
        val seconds = if (effectiveBps > 0) totalBits / effectiveBps else 0.0

        val days = (seconds / 86400).toInt()
        val hours = ((seconds % 86400) / 3600).toInt()
        val mins = ((seconds % 3600) / 60).toInt()
        val secs = (seconds % 60).toInt()

        val timeFormatted = buildString {
            if (days > 0) append("$days d ")
            if (hours > 0 || days > 0) append("$hours h ")
            if (mins > 0 || hours > 0 || days > 0) append("$mins m ")
            append("$secs s")
        }

        return TransferResult(
            totalSeconds = seconds,
            formattedTime = timeFormatted,
            bitsTransferred = totalBits
        )
    }
}

object MtuMssCalculator {
    data class MtuResult(
        val mtu: Int,
        val ipHeader: Int,
        val tcpHeader: Int,
        val vpnOverhead: Int,
        val mss: Int,
        val explanation: String
    )

    fun calculate(
        mtu: Int,
        isIpv6: Boolean,
        vpnType: String = "None" // None, IPsec, WireGuard, GRE
    ): MtuResult {
        val ipHeader = if (isIpv6) 40 else 20
        val tcpHeader = 20
        val vpnOverhead = when (vpnType) {
            "IPsec ESP" -> 56
            "WireGuard" -> 60
            "GRE" -> 24
            "PPPoE" -> 8
            else -> 0
        }

        val mss = (mtu - ipHeader - tcpHeader - vpnOverhead).coerceAtLeast(0)
        val explanation = "MSS = MTU ($mtu) - IP ($ipHeader) - TCP ($tcpHeader)" +
                (if (vpnOverhead > 0) " - $vpnType Overhead ($vpnOverhead)" else "") +
                " = $mss Bytes"

        return MtuResult(
            mtu = mtu,
            ipHeader = ipHeader,
            tcpHeader = tcpHeader,
            vpnOverhead = vpnOverhead,
            mss = mss,
            explanation = explanation
        )
    }
}

object MacTools {
    data class MacAnalysis(
        val colonFormat: String,
        val dashFormat: String,
        val ciscoFormat: String,
        val isBroadcast: Boolean,
        val isMulticast: Boolean,
        val isLocallyAdministered: Boolean,
        val isValid: Boolean
    )

    fun analyze(macInput: String): Result<MacAnalysis> {
        val clean = macInput.replace(Regex("[:.-]"), "").trim().lowercase()
        if (clean.length != 12 || clean.any { it !in "0123456789abcdef" }) {
            return Result.failure(IllegalArgumentException("Invalid MAC format (Must be 12 hexadecimal characters)"))
        }

        val colon = clean.chunked(2).joinToString(":")
        val dash = clean.chunked(2).joinToString("-")
        val cisco = clean.chunked(4).joinToString(".")

        val firstOctet = clean.substring(0, 2).toInt(16)
        val isMulticast = (firstOctet and 0x01) == 0x01
        val isLocal = (firstOctet and 0x02) == 0x02
        val isBroadcast = clean == "ffffffffffff"

        return Result.success(
            MacAnalysis(
                colonFormat = colon.uppercase(),
                dashFormat = dash.uppercase(),
                ciscoFormat = cisco,
                isBroadcast = isBroadcast,
                isMulticast = isMulticast,
                isLocallyAdministered = isLocal,
                isValid = true
            )
        )
    }
}

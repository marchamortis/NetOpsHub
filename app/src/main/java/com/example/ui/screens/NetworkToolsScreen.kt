package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.HttpErrorEntity
import com.example.data.local.PortEntity
import com.example.data.local.WiresharkEntity
import com.example.networktools.*
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassToolbar
import com.example.ui.components.SearchInput
import com.example.ui.theme.LocalNetOpsColors

enum class ToolType(val titleRes: Int) {
    SUBNET(R.string.tool_subnet_calc),
    VLSM(R.string.tool_vlsm_calc),
    WILDCARD(R.string.tool_wildcard_calc),
    IPV6(R.string.tool_ipv6),
    BANDWIDTH(R.string.tool_bandwidth),
    MTU_MSS(R.string.tool_mtu_mss),
    MAC(R.string.tool_mac),
    PORTS(R.string.tool_ports),
    HTTP_ERRORS(R.string.tool_http_errors),
    WIRESHARK(R.string.tool_wireshark)
}

@Composable
fun NetworkToolsScreen(
    ports: List<PortEntity>,
    httpErrors: List<HttpErrorEntity>,
    wiresharkFilters: List<WiresharkEntity>,
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var selectedTool by remember { mutableStateOf(ToolType.SUBNET) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = "Network Engineering Tools",
            subtitle = "Academy • 10 Precision Calculators & References",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("tools_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            }
        )

        // Tool Selector Tabs / Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ToolType.values()) { tool ->
                GlassChip(
                    text = stringResource(tool.titleRes),
                    selected = tool == selectedTool,
                    onClick = { selectedTool = tool }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTool) {
                ToolType.SUBNET -> SubnetCalculatorTool()
                ToolType.VLSM -> VlsmCalculatorTool()
                ToolType.WILDCARD -> WildcardCalculatorTool()
                ToolType.IPV6 -> Ipv6CalculatorTool()
                ToolType.BANDWIDTH -> BandwidthCalculatorTool()
                ToolType.MTU_MSS -> MtuMssCalculatorTool()
                ToolType.MAC -> MacAddressTool()
                ToolType.PORTS -> PortsReferenceTool(ports)
                ToolType.HTTP_ERRORS -> HttpErrorsReferenceTool(httpErrors)
                ToolType.WIRESHARK -> WiresharkReferenceTool(wiresharkFilters)
            }
        }
    }
}

// 1. IPv4 Subnet Calculator Tool
@Composable
private fun SubnetCalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var ipInput by remember { mutableStateOf("192.168.1.0") }
    var prefixInput by remember { mutableStateOf("24") }
    var result by remember { mutableStateOf<SubnetCalculator.SubnetResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        SubnetCalculator.calculate("192.168.1.0", 24).onSuccess { result = it }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.tool_subnet_calc),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("IPv4 Address (e.g. 192.168.10.1)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("subnet_ip_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = prefixInput,
                    onValueChange = { prefixInput = it },
                    label = { Text("Prefix Length (0 - 32)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("subnet_prefix_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val p = prefixInput.toIntOrNull() ?: 24
                        SubnetCalculator.calculate(ipInput, p)
                            .onSuccess {
                                result = it
                                errorMessage = null
                            }
                            .onFailure {
                                errorMessage = it.message
                                result = null
                            }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("subnet_calculate_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                ) {
                    Text(stringResource(R.string.calc_calculate), color = tokens.background, fontWeight = FontWeight.Bold)
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (result != null) {
            val r = result!!
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "Calculation Output (/${r.prefix})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.accent)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CalcRow("Network Address", r.networkAddress)
                    CalcRow("Broadcast Address", r.broadcastAddress)
                    CalcRow("Subnet Mask", r.subnetMask)
                    CalcRow("Wildcard Mask", r.wildcardMask)
                    CalcRow("First Usable Host", r.firstUsableHost)
                    CalcRow("Last Usable Host", r.lastUsableHost)
                    CalcRow("Usable Hosts", "${r.usableHosts}")
                    CalcRow("Total Addresses", "${r.totalAddresses}")
                    CalcRow("Binary IP", r.binaryIp)
                    CalcRow("Binary Mask", r.binaryMask)
                }
            }
        }
    }
}

// 2. VLSM Calculator Tool
@Composable
private fun VlsmCalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var baseIp by remember { mutableStateOf("192.168.1.0") }
    var basePrefix by remember { mutableStateOf("24") }
    var hostReqsText by remember { mutableStateOf("100, 50, 25, 10") }
    var vlsmResult by remember { mutableStateOf<VlsmCalculator.VlsmResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.tool_vlsm_calc),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = baseIp,
                        onValueChange = { baseIp = it },
                        label = { Text("Base Network IP") },
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = basePrefix,
                        onValueChange = { basePrefix = it },
                        label = { Text("Prefix (CIDR)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hostReqsText,
                    onValueChange = { hostReqsText = it },
                    label = { Text("Required Hosts (Comma separated: e.g. 100, 50, 25, 10)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val bp = basePrefix.toIntOrNull() ?: 24
                        val reqs = hostReqsText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .mapIndexed { i, count -> "Subnet-${i + 1}" to count }

                        if (reqs.isEmpty()) {
                            error = "Enter valid comma-separated host counts"
                            return@Button
                        }

                        VlsmCalculator.calculate(baseIp, bp, reqs)
                            .onSuccess {
                                vlsmResult = it
                                error = null
                            }
                            .onFailure {
                                error = it.message
                                vlsmResult = null
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                ) {
                    Text("Optimize & Allocate VLSM", color = tokens.background, fontWeight = FontWeight.Bold)
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (vlsmResult != null) {
            val res = vlsmResult!!
            item {
                Text(
                    text = "Allocated Subnets (${res.allocations.size} subnets):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.accent)
                )
            }

            items(res.allocations) { alloc ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${alloc.name} (/${alloc.prefix})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary)
                        )
                        Text(
                            text = "Usable: ${alloc.allocatedHosts} (Req: ${alloc.requiredHosts})",
                            style = MaterialTheme.typography.labelSmall.copy(color = tokens.accent, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    CalcRow("Network", alloc.networkAddress)
                    CalcRow("Mask", alloc.subnetMask)
                    CalcRow("Usable Range", "${alloc.firstUsableHost} - ${alloc.lastUsableHost}")
                    CalcRow("Broadcast", alloc.broadcastAddress)
                }
            }

            item {
                Text(
                    text = "Address Space Utilization: ${res.remainingSpace}",
                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textMuted, fontFamily = FontFamily.Monospace)
                )
            }
        }
    }
}

// 3. Wildcard Mask Converter Tool
@Composable
private fun WildcardCalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var maskInput by remember { mutableStateOf("255.255.255.0") }
    var wildcardResult by remember { mutableStateOf("0.0.0.255") }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Wildcard Mask Calculator (ACLs)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = maskInput,
                    onValueChange = {
                        maskInput = it
                        WildcardCalculator.maskToWildcard(it).onSuccess { wildcardResult = it }
                    },
                    label = { Text("Subnet Mask (e.g. 255.255.255.224)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                CalcRow("Wildcard Mask", wildcardResult)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cisco IOS ACL Example:\naccess-list 10 permit 192.168.1.0 $wildcardResult",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = tokens.accent)
                )
            }
        }
    }
}

// 4. IPv6 Tools
@Composable
private fun Ipv6CalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var ipv6Input by remember { mutableStateOf("2001:db8::1") }
    var analysis by remember { mutableStateOf<Ipv6Tools.Ipv6Analysis?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Ipv6Tools.analyze(ipv6Input).onSuccess { analysis = it }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "IPv6 Expansion & Type Inspector", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = ipv6Input,
                    onValueChange = {
                        ipv6Input = it
                        Ipv6Tools.analyze(it)
                            .onSuccess { analysis = it; error = null }
                            .onFailure { error = it.message; analysis = null }
                    },
                    label = { Text("IPv6 Address (e.g. fe80::1 or 2001:db8::1)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }

                if (analysis != null) {
                    val a = analysis!!
                    Spacer(modifier = Modifier.height(12.dp))
                    CalcRow("Expanded (RFC)", a.expanded)
                    CalcRow("Compressed (RFC 5952)", a.compressed)
                    CalcRow("Address Type", a.addressType)
                    CalcRow("Scope", a.scope)
                }
            }
        }
    }
}

// 5. Bandwidth & Transfer Time Calculator
@Composable
private fun BandwidthCalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var sizeVal by remember { mutableStateOf("10") }
    var sizeUnit by remember { mutableStateOf("GB") }
    var speedVal by remember { mutableStateOf("100") }
    var speedUnit by remember { mutableStateOf("Mbps") }
    var transferResult by remember { mutableStateOf("") }

    fun recalc() {
        val sv = sizeVal.toDoubleOrNull() ?: 1.0
        val sp = speedVal.toDoubleOrNull() ?: 100.0
        val res = BandwidthCalculator.calculate(sv, sizeUnit, sp, speedUnit)
        transferResult = res.formattedTime
    }

    LaunchedEffect(Unit) { recalc() }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Bandwidth & File Transfer Time", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sizeVal,
                        onValueChange = { sizeVal = it; recalc() },
                        label = { Text("File Size") },
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = sizeUnit,
                        onValueChange = { sizeUnit = it; recalc() },
                        label = { Text("Unit (MB/GB/TB)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = speedVal,
                        onValueChange = { speedVal = it; recalc() },
                        label = { Text("Link Speed") },
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = speedUnit,
                        onValueChange = { speedUnit = it; recalc() },
                        label = { Text("Unit (Mbps/Gbps)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                CalcRow("Estimated Transfer Time (90% Eff)", transferResult)
            }
        }
    }
}

// 6. MTU / MSS Calculator Tool
@Composable
private fun MtuMssCalculatorTool() {
    val tokens = LocalNetOpsColors.current
    var mtuVal by remember { mutableStateOf("1500") }
    var isIpv6 by remember { mutableStateOf(false) }
    var vpnType by remember { mutableStateOf("None") }
    var result by remember { mutableStateOf<MtuMssCalculator.MtuResult?>(null) }

    fun recalc() {
        val m = mtuVal.toIntOrNull() ?: 1500
        result = MtuMssCalculator.calculate(m, isIpv6, vpnType)
    }

    LaunchedEffect(isIpv6, vpnType) { recalc() }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "MTU / MSS & VPN Overhead Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = mtuVal,
                    onValueChange = { mtuVal = it; recalc() },
                    label = { Text("Interface MTU (e.g. 1500, 1492, 9000)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isIpv6, onCheckedChange = { isIpv6 = it })
                    Text("IPv6 Header (40B instead of IPv4 20B)", color = tokens.textPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("VPN Overhead:", style = MaterialTheme.typography.labelMedium.copy(color = tokens.textSecondary))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(listOf("None", "IPsec ESP", "WireGuard", "GRE", "PPPoE")) { type ->
                        GlassChip(text = type, selected = vpnType == type, onClick = { vpnType = type })
                    }
                }

                if (result != null) {
                    val r = result!!
                    Spacer(modifier = Modifier.height(14.dp))
                    CalcRow("Maximum Segment Size (MSS)", "${r.mss} Bytes")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = r.explanation, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = tokens.accent))
                }
            }
        }
    }
}

// 7. MAC Address Tools
@Composable
private fun MacAddressTool() {
    val tokens = LocalNetOpsColors.current
    var macInput by remember { mutableStateOf("00:1A:2B:3C:4D:5E") }
    var analysis by remember { mutableStateOf<MacTools.MacAnalysis?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        MacTools.analyze(macInput).onSuccess { analysis = it }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "MAC Address Formatter & Validator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = macInput,
                    onValueChange = {
                        macInput = it
                        MacTools.analyze(it)
                            .onSuccess { analysis = it; error = null }
                            .onFailure { error = it.message; analysis = null }
                    },
                    label = { Text("Enter MAC (Colon, Dash or Cisco format)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }

                if (analysis != null) {
                    val m = analysis!!
                    Spacer(modifier = Modifier.height(12.dp))
                    CalcRow("Standard (Colon)", m.colonFormat)
                    CalcRow("Windows (Dash)", m.dashFormat)
                    CalcRow("Cisco IOS Format", m.ciscoFormat)
                    CalcRow("Multicast Frame?", if (m.isMulticast) "YES (Multicast)" else "NO (Unicast)")
                    CalcRow("Locally Administered?", if (m.isLocallyAdministered) "YES (LAA)" else "NO (Globally Unique OUI)")
                    CalcRow("Broadcast Frame?", if (m.isBroadcast) "YES" else "NO")
                }
            }
        }
    }
}

// 8. TCP/UDP Ports Reference Tool
@Composable
private fun PortsReferenceTool(ports: List<PortEntity>) {
    val tokens = LocalNetOpsColors.current
    var search by remember { mutableStateOf("") }
    val filtered = remember(ports, search) {
        if (search.isBlank()) ports else ports.filter {
            it.portNumber.toString().contains(search) ||
            it.service.contains(search, ignoreCase = true) ||
            it.description.contains(search, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchInput(value = search, onValueChange = { search = it }, placeholder = "Search port (e.g. 53, RDP, 7300)...")
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            items(filtered, key = { it.id }) { p ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Port ${p.portNumber} (${p.protocol})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                        Text(text = p.service, style = MaterialTheme.typography.labelSmall.copy(color = tokens.accent, fontWeight = FontWeight.Bold))
                    }
                    Text(text = p.description, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    if (p.securityNotes.isNotBlank()) {
                        Text(text = "Security: ${p.securityNotes}", style = MaterialTheme.typography.labelSmall.copy(color = tokens.textMuted))
                    }
                }
            }
        }
    }
}

// 9. HTTP Errors Reference Tool
@Composable
private fun HttpErrorsReferenceTool(httpErrors: List<HttpErrorEntity>) {
    val tokens = LocalNetOpsColors.current
    var search by remember { mutableStateOf("") }
    val filtered = remember(httpErrors, search) {
        if (search.isBlank()) httpErrors else httpErrors.filter {
            it.code.toString().contains(search) || it.name.contains(search, ignoreCase = true) || it.meaning.contains(search, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchInput(value = search, onValueChange = { search = it }, placeholder = "Search HTTP status (e.g. 401, 404, 502)...")
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            items(filtered, key = { it.id }) { err ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "${err.code} ${err.name}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                        Text(text = err.category, style = MaterialTheme.typography.labelSmall.copy(color = tokens.accent))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = err.meaning, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Troubleshooting: ${err.troubleshooting}", style = MaterialTheme.typography.labelSmall.copy(color = tokens.textMuted))
                }
            }
        }
    }
}

// 10. Wireshark Reference Tool
@Composable
private fun WiresharkReferenceTool(filters: List<WiresharkEntity>) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    val filtered = remember(filters, search) {
        if (search.isBlank()) filters else filters.filter {
            it.filterSyntax.contains(search, ignoreCase = true) ||
            it.title.contains(search, ignoreCase = true) ||
            it.description.contains(search, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchInput(value = search, onValueChange = { search = it }, placeholder = "Search filter (e.g. syn, retransmission, 3389)...")
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            items(filtered, key = { it.id }) { ws ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = ws.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary))
                        IconButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("Wireshark Filter", ws.filterSyntax))
                                Toast.makeText(context, "Filter copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Filter", tint = tokens.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.surface,
                        border = BorderStroke(0.5.dp, tokens.glassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = ws.filterSyntax,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = tokens.accent, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = ws.description, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    Text(text = "Use Case: ${ws.useCase}", style = MaterialTheme.typography.labelSmall.copy(color = tokens.textMuted))
                }
            }
        }
    }
}

@Composable
private fun CalcRow(label: String, value: String) {
    val tokens = LocalNetOpsColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = tokens.textSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
        )
    }
}

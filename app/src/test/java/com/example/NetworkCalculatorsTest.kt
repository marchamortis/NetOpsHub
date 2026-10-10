package com.example

import com.example.networktools.*
import org.junit.Assert.*
import org.junit.Test

class NetworkCalculatorsTest {

    @Test
    fun testSubnetCalculator_slash24() {
        val result = SubnetCalculator.calculate("192.168.1.55", 24).getOrThrow()
        assertEquals("192.168.1.0", result.networkAddress)
        assertEquals("192.168.1.255", result.broadcastAddress)
        assertEquals("255.255.255.0", result.subnetMask)
        assertEquals("0.0.0.255", result.wildcardMask)
        assertEquals("192.168.1.1", result.firstUsableHost)
        assertEquals("192.168.1.254", result.lastUsableHost)
        assertEquals(254L, result.usableHosts)
        assertEquals(256L, result.totalAddresses)
    }

    @Test
    fun testSubnetCalculator_pointToPoint_slash30_and_31() {
        // /30: 2 usable hosts
        val res30 = SubnetCalculator.calculate("10.0.0.1", 30).getOrThrow()
        assertEquals("10.0.0.0", res30.networkAddress)
        assertEquals("10.0.0.3", res30.broadcastAddress)
        assertEquals("10.0.0.1", res30.firstUsableHost)
        assertEquals("10.0.0.2", res30.lastUsableHost)
        assertEquals(2L, res30.usableHosts)

        // /31: RFC 3021
        val res31 = SubnetCalculator.calculate("10.0.0.0", 31).getOrThrow()
        assertEquals(2L, res31.usableHosts)
        assertEquals("10.0.0.0", res31.firstUsableHost)
        assertEquals("10.0.0.1", res31.lastUsableHost)

        // /32: Single host
        val res32 = SubnetCalculator.calculate("10.0.0.5", 32).getOrThrow()
        assertEquals(1L, res32.usableHosts)
        assertEquals("10.0.0.5", res32.firstUsableHost)
        assertEquals("10.0.0.5", res32.lastUsableHost)
    }

    @Test
    fun testVlsmCalculator_allocation() {
        val reqs = listOf(
            "Sales" to 100,
            "IT" to 50,
            "Dev" to 25,
            "HR" to 10
        )
        val res = VlsmCalculator.calculate("192.168.1.0", 24, reqs).getOrThrow()
        assertEquals(4, res.allocations.size)

        // Sales (100 hosts) -> /25 (126 usable)
        assertEquals(25, res.allocations[0].prefix)
        assertEquals("192.168.1.0", res.allocations[0].networkAddress)
        assertEquals("192.168.1.127", res.allocations[0].broadcastAddress)

        // IT (50 hosts) -> /26 (62 usable)
        assertEquals(26, res.allocations[1].prefix)
        assertEquals("192.168.1.128", res.allocations[1].networkAddress)
        assertEquals("192.168.1.191", res.allocations[1].broadcastAddress)

        // Dev (25 hosts) -> /27 (30 usable)
        assertEquals(27, res.allocations[2].prefix)
        assertEquals("192.168.1.192", res.allocations[2].networkAddress)
        assertEquals("192.168.1.223", res.allocations[2].broadcastAddress)

        // HR (10 hosts) -> /28 (14 usable)
        assertEquals(28, res.allocations[3].prefix)
        assertEquals("192.168.1.224", res.allocations[3].networkAddress)
        assertEquals("192.168.1.239", res.allocations[3].broadcastAddress)
    }

    @Test
    fun testWildcardCalculator() {
        assertEquals("0.0.0.255", WildcardCalculator.maskToWildcard("255.255.255.0").getOrThrow())
        assertEquals("0.0.0.15", WildcardCalculator.maskToWildcard("255.255.255.240").getOrThrow())
        assertEquals("255.255.255.0", WildcardCalculator.wildcardToMask("0.0.0.255").getOrThrow())
    }

    @Test
    fun testIpv6Tools() {
        val analysis = Ipv6Tools.analyze("2001:db8::1").getOrThrow()
        assertEquals("2001:0db8:0000:0000:0000:0000:0000:0001", analysis.expanded)
        assertEquals("2001:db8::1", analysis.compressed)
        assertTrue(analysis.addressType.contains("Global Unicast"))

        val loopback = Ipv6Tools.analyze("::1").getOrThrow()
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0001", loopback.expanded)
        assertTrue(loopback.addressType.contains("Loopback"))

        val linkLocal = Ipv6Tools.analyze("fe80::200:5aee:feaa:20a2").getOrThrow()
        assertTrue(linkLocal.addressType.contains("Link-Local"))
    }

    @Test
    fun testMtuMssCalculator() {
        // Standard IPv4: 1500 - 20 (IP) - 20 (TCP) = 1460
        val std = MtuMssCalculator.calculate(1500, isIpv6 = false, vpnType = "None")
        assertEquals(1460, std.mss)

        // WireGuard on IPv4: 1500 - 20 - 20 - 60 = 1400
        val wg = MtuMssCalculator.calculate(1500, isIpv6 = false, vpnType = "WireGuard")
        assertEquals(1400, wg.mss)

        // IPsec ESP on IPv6: 1500 - 40 - 20 - 56 = 1384
        val ipsec6 = MtuMssCalculator.calculate(1500, isIpv6 = true, vpnType = "IPsec ESP")
        assertEquals(1384, ipsec6.mss)
    }

    @Test
    fun testMacTools() {
        val mac = MacTools.analyze("00-1A-2B-3C-4D-5E").getOrThrow()
        assertEquals("00:1A:2B:3C:4D:5E", mac.colonFormat)
        assertEquals("00-1A-2B-3C-4D-5E", mac.dashFormat)
        assertEquals("001a.2b3c.4d5e", mac.ciscoFormat)
        assertFalse(mac.isMulticast)
        assertFalse(mac.isBroadcast)

        val broadcast = MacTools.analyze("ff:ff:ff:ff:ff:ff").getOrThrow()
        assertTrue(broadcast.isBroadcast)

        val multicast = MacTools.analyze("01:00:5e:00:00:01").getOrThrow()
        assertTrue(multicast.isMulticast)
    }

    @Test
    fun testRbacRoleHierarchy() {
        // Creator is highest role
        val creator = com.example.data.model.UserRole.fromString("Creator")
        assertEquals(com.example.data.model.UserRole.CREATOR, creator)

        val admin = com.example.data.model.UserRole.fromString("Admin")
        assertEquals(com.example.data.model.UserRole.ADMIN, admin)

        val editor = com.example.data.model.UserRole.fromString("Editor")
        assertEquals(com.example.data.model.UserRole.EDITOR, editor)

        val viewer = com.example.data.model.UserRole.fromString("Viewer")
        assertEquals(com.example.data.model.UserRole.VIEWER, viewer)

        // Verify fallback
        val unknown = com.example.data.model.UserRole.fromString("guest")
        assertEquals(com.example.data.model.UserRole.VIEWER, unknown)
    }
}

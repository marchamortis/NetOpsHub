const fs = require("fs");
const content = fs.readFileSync("app/src/main/java/com/example/data/local/SeedData.kt", "utf8");
const match = content.match(/val servers = listOf\(([\s\S]*?)\n    \)/);
if (!match) {
  console.error("No match for servers");
  process.exit(1);
}
const lines = match[1].split("\n").filter(l => l.includes("ServerEntity"));
const sqls = [];
for (const line of lines) {
  const m = line.match(/ServerEntity\("([^"]+)",\s*(\d+),\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)"(?:,\s*"([^"]*)")?(?:,\s*"([^"]*)")?\)/);
  if (m) {
    const [_, id, num, vc, host, name, ip, srv, rdp, status, site, os, notes] = m;
    const cleanOs = (os || "Windows/Linux").replace(/'/g, "''");
    const cleanNotes = (notes || "").replace(/'/g, "''");
    const cleanName = name.replace(/'/g, "''");
    const cleanSrv = srv.replace(/'/g, "''");
    sqls.push(`INSERT OR REPLACE INTO servers (id, server_num, vcenter, server_host, name, ip, service, port_rdp, status, site, os, notes) VALUES ('${id}', ${num}, '${vc}', '${host}', '${cleanName}', '${ip}', '${cleanSrv}', '${rdp}', '${status}', '${site}', '${cleanOs}', '${cleanNotes}');`);
  }
}
fs.writeFileSync("backend/seed_servers.sql", sqls.join("\n"));
console.log("Successfully generated " + sqls.length + " server statements into backend/seed_servers.sql");

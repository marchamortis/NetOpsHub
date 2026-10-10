const fs = require("fs");
const content = fs.readFileSync("app/src/main/java/com/example/data/local/SeedData.kt", "utf8");
const match = content.match(/val articles = listOf\(([\s\S]*?)\n    \)\n\n    \/\/ Initial Default Users/);
if (!match) {
  console.error("No match for articles");
  process.exit(1);
}

// Extract ArticleEntities
const regex = /ArticleEntity\(\s*id\s*=\s*"([^"]+)",\s*title\s*=\s*"([^"]+)",\s*titleFa\s*=\s*"([^"]*)",\s*summary\s*=\s*"([^"]*)",\s*summaryFa\s*=\s*"([^"]*)",\s*content\s*=\s*"([^"]*)",\s*categorySlug\s*=\s*"([^"]*)",\s*subcategory\s*=\s*"([^"]*)",\s*vendor\s*=\s*"([^"]*)",\s*difficulty\s*=\s*"([^"]*)",\s*tags\s*=\s*"([^"]*)"/g;

let m;
const sqls = [];
while ((m = regex.exec(match[1])) !== null) {
  const [_, id, title, titleFa, summary, summaryFa, artContent, cat, subcat, vendor, diff, tags] = m;
  const esc = (s) => (s || "").replace(/'/g, "''");
  sqls.push(`INSERT OR REPLACE INTO academy_articles (id, title, title_fa, summary, summary_fa, content, category_slug, subcategory, vendor, difficulty, tags) VALUES ('${id}', '${esc(title)}', '${esc(titleFa)}', '${esc(summary)}', '${esc(summaryFa)}', '${esc(artContent)}', '${cat}', '${esc(subcat)}', '${esc(vendor)}', '${diff}', '${esc(tags)}');`);
}

fs.writeFileSync("backend/seed_articles.sql", sqls.join("\n"));
console.log("Successfully generated " + sqls.length + " article statements into backend/seed_articles.sql");

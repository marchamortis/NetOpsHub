package com.example.data.io

import android.content.Context
import android.net.Uri
import com.example.data.local.PhoneEntryEntity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * ExcelImporter (v2.0.1 - دفترچه تلفن)
 *
 * Imports contacts for the Phone Book from:
 *  * Microsoft Excel .xlsx workbooks (Office Open XML) - parsed natively with
 *    ZipInputStream + XmlPullParser, zero third-party dependencies.
 *  * CSV files (comma / semicolon separated, UTF-8 with or without BOM).
 *
 * Column mapping is header-driven with English + Persian header synonyms:
 *   name/first name/نام | family/last name/نام خانوادگی | phone/mobile/شماره/تلفن
 *   company/شرکت | extension/internal/داخلی/شماره داخلی
 * If no recognizable header row exists, the positional fallback order is:
 *   First name | Last name | Phone | Company | Extension
 */
object ExcelImporter {

    data class ImportResult(
        val entries: List<PhoneEntryEntity>,
        val skipped: Int,
        val detectedColumns: List<String>
    )

    fun import(context: Context, uri: Uri): ImportResult {
        val stream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open the selected file")
        stream.use { input ->
            val name = getFileName(context, uri).lowercase()
            return if (name.endsWith(".xlsx")) {
                parseXlsx(input)
            } else {
                // .csv, .txt or anything else -> CSV parser
                parseCsv(input)
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result = "import.xlsx"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && cursor.moveToFirst()) result = cursor.getString(idx)
        } ?: run { result = uri.lastPathSegment ?: "import.xlsx" }
        return result
    }

    // ==================================================================
    // XLSX (minimal but robust Office Open XML reader)
    // ==================================================================

    private fun parseXlsx(input: InputStream): ImportResult {
        val sharedStrings = mutableListOf<String>()
        val sheetXml = StringBuilder()

        ZipInputStream(input).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                when {
                    entry.name == "xl/sharedStrings.xml" ->
                        sharedStrings.addAll(readSharedStrings(zis))
                    entry.name == "xl/worksheets/sheet1.xml" ||
                        entry.name.matches(Regex("xl/worksheets/sheet\\d+\\.xml")) -> {
                        if (sheetXml.isEmpty()) {
                            sheetXml.append(zis.readBytes().toString(Charsets.UTF_8))
                        }
                    }
                }
                entry = zis.nextEntry
            }
        }

        if (sheetXml.isEmpty()) throw IllegalArgumentException("No worksheet found inside the .xlsx file")

        val rows = parseSheetRows(sheetXml.toString(), sharedStrings)
        return rowsToEntries(rows)
    }

    private fun readSharedStrings(stream: InputStream): List<String> {
        val strings = mutableListOf<String>()
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(stream, null)
        var event = parser.eventType
        var current = StringBuilder()
        var insideT = false
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "si" -> current = StringBuilder()
                    "t" -> insideT = true
                }
                XmlPullParser.TEXT -> if (insideT) current.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "t" -> insideT = false
                    "si" -> strings.add(current.toString())
                }
            }
            event = parser.next()
        }
        return strings
    }

    private data class CellRef(val row: Int, val col: Int)

    private fun parseCellRef(ref: String): CellRef {
        val colPart = ref.takeWhile { it.isLetter() }
        val rowPart = ref.dropWhile { it.isLetter() }
        var col = 0
        for (c in colPart) col = col * 26 + (c.uppercaseChar() - 'A' + 1)
        return CellRef((rowPart.toIntOrNull() ?: 0) - 1, col - 1)
    }

    private fun parseSheetRows(xml: String, sharedStrings: List<String>): List<List<String>> {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(xml.reader())
        val rows = mutableListOf<MutableList<String>>()
        var event = parser.eventType
        var currentRow = -1
        var currentCell = -1
        var cellIsSharedString = false
        var cellValue = StringBuilder()

        fun commitCell() {
            if (currentRow >= 0 && currentCell >= 0) {
                while (rows.size <= currentRow) rows.add(mutableListOf())
                while (rows[currentRow].size <= currentCell) rows[currentRow].add("")
                val raw = cellValue.toString().trim()
                val value = if (cellIsSharedString) {
                    raw.toIntOrNull()?.let { sharedStrings.getOrNull(it) } ?: raw
                } else raw
                rows[currentRow][currentCell] = value
            }
            cellValue = StringBuilder()
        }

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "row" -> {
                        currentRow = parser.getAttributeValue(null, "r")?.toIntOrNull()?.minus(1) ?: rows.size
                        while (rows.size <= currentRow) rows.add(mutableListOf())
                    }
                    "c" -> {
                        commitCell()
                        val ref = parser.getAttributeValue(null, "r") ?: ""
                        val cellRef = if (ref.isNotEmpty()) parseCellRef(ref) else CellRef(currentRow, rows.getOrNull(currentRow)?.size ?: 0)
                        currentCell = cellRef.col
                        cellIsSharedString = parser.getAttributeValue(null, "t") == "s"
                    }
                }
                XmlPullParser.TEXT -> cellValue.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "c" -> commitCell()
                }
            }
            event = parser.next()
        }
        commitCell()

        return rows
    }

    // ==================================================================
    // CSV
    // ==================================================================

    private fun parseCsv(input: InputStream): ImportResult {
        val text = input.readBytes().toString(Charsets.UTF_8).removePrefix("\uFEFF")
        val rows = mutableListOf<List<String>>()
        var field = StringBuilder()
        var row = mutableListOf<String>()
        var inQuotes = false
        var i = 0
        val delimiters = mutableSetOf<Char>()
        // Detect delimiter on the first line (comma vs semicolon vs tab)
        val firstLine = text.lineSequence().firstOrNull() ?: ""
        val delimiter = when {
            firstLine.count { it == ';' } > firstLine.count { it == ',' } -> ';'
            firstLine.count { it == '\t' } > firstLine.count { it == ',' } -> '\t'
            else -> ','
        }
        delimiters.add(delimiter)

        while (i < text.length) {
            val c = text[i]
            when {
                inQuotes && c == '"' -> {
                    if (i + 1 < text.length && text[i + 1] == '"') { field.append('"'); i++ } else inQuotes = false
                }
                c == '"' -> inQuotes = true
                c == delimiter && !inQuotes -> { row.add(field.toString().trim()); field = StringBuilder() }
                c == '\n' && !inQuotes -> { row.add(field.toString().trim()); field = StringBuilder(); if (row.any { it.isNotBlank() }) rows.add(row); row = mutableListOf() }
                c != '\r' -> field.append(c)
            }
            i++
        }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString().trim())
            if (row.any { it.isNotBlank() }) rows.add(row)
        }
        return rowsToEntries(rows)
    }

    // ==================================================================
    // Header detection + entity mapping
    // ==================================================================

    private val FIRST_NAME = listOf("firstname", "first_name", "first", "name", "نام")
    private val LAST_NAME = listOf("lastname", "last_name", "last", "family", "surname", "فامیلی", "فامیل", "نام خانوادگی")
    private val PHONE = listOf("phone", "mobile", "tel", "telephone", "cell", "شماره", "موبایل", "تلفن", "همراه")
    private val COMPANY = listOf("company", "org", "organization", "شرکت", "سازمان", "واحد")
    private val EXTENSION = listOf("extension", "ext", "internal", "sip", "داخلی", "شماره داخلی", "سنترال")

    private fun normalize(h: String) = h.trim().lowercase()
        .replace(" ", "").replace("_", "").replace("-", "").replace("‌", "")

    private fun findColumn(headers: List<String>, candidates: List<String>): Int {
        for (cand in candidates) {
            val idx = headers.indexOfFirst { normalize(it) == normalize(cand) }
            if (idx >= 0) return idx
        }
        for (cand in candidates) {
            val idx = headers.indexOfFirst { normalize(it).contains(normalize(cand)) }
            if (idx >= 0) return idx
        }
        return -1
    }

    private fun rowsToEntries(rawRows: List<List<String>>): ImportResult {
        if (rawRows.isEmpty()) return ImportResult(emptyList(), 0, emptyList())

        var dataRows = rawRows
        var colName = -1; var colFamily = -1; var colPhone = -1; var colCompany = -1; var colExt = -1

        val headerRow = rawRows.first()
        colName = findColumn(headerRow, FIRST_NAME)
        if (colName >= 0) {
            colFamily = findColumn(headerRow, LAST_NAME)
            colPhone = findColumn(headerRow, PHONE)
            colCompany = findColumn(headerRow, COMPANY)
            colExt = findColumn(headerRow, EXTENSION)
            dataRows = rawRows.drop(1)
        } else {
            // positional fallback: First | Last | Phone | Company | Extension
            colName = 0; colFamily = 1; colPhone = 2; colCompany = 3; colExt = 4
        }

        val entries = mutableListOf<PhoneEntryEntity>()
        var skipped = 0
        var counter = 0L
        for (r in dataRows) {
            fun cell(idx: Int): String = if (idx in r.indices) r[idx].trim() else ""
            val first = cell(colName)
            if (first.isBlank()) { skipped++; continue }
            entries.add(
                PhoneEntryEntity(
                    id = "ph_${System.currentTimeMillis()}_${counter++}",
                    firstName = first.take(80),
                    lastName = cell(colFamily).take(80),
                    phone = cell(colPhone).take(32),
                    company = cell(colCompany).take(120),
                    extension = cell(colExt).take(16),
                    notes = ""
                )
            )
        }
        val detected = listOfNotNull(
            if (colName >= 0) "First Name" else null,
            if (colFamily >= 0) "Last Name" else null,
            if (colPhone >= 0) "Phone" else null,
            if (colCompany >= 0) "Company" else null,
            if (colExt >= 0) "Extension" else null
        )
        return ImportResult(entries, skipped, detected)
    }
}

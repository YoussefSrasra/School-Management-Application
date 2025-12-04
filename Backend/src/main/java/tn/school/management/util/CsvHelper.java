package tn.school.management.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import tn.school.management.dto.StudentDto;
import tn.school.management.entity.Level;
import tn.school.management.entity.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvHelper {

    public static String TYPE = "text/csv";
    static String[] HEADERS = { "Id", "Username", "Level" };

    // Check if uploaded file is actually CSV
    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType()) || file.getOriginalFilename().endsWith(".csv");
    }

    // EXPORT: Convert List<StudentDto> to InputStream (for download)
    public static ByteArrayInputStream studentsToCsv(List<StudentDto> students) {
        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setQuoteMode(org.apache.commons.csv.QuoteMode.MINIMAL)
                .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (StudentDto student : students) {
                List<String> data = Arrays.asList(
                        String.valueOf(student.getId()),
                        student.getUsername(),
                        student.getLevel().name()
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    // IMPORT: Convert InputStream (file content) to List<Student>
    public static List<Student> csvToStudents(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            List<Student> students = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                // Basic validation: skip if username is missing
                if (csvRecord.get("Username") == null || csvRecord.get("Username").isBlank()) {
                    continue;
                }

                Student student = Student.builder()
                        .username(csvRecord.get("Username"))
                        .level(Level.valueOf(csvRecord.get("Level"))) // Assumes Level matches Enum exactly
                        .build();

                students.add(student);
            }

            return students;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
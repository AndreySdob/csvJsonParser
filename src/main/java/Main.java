import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // CSV файл для чтения
        String csvFileName = "data.csv";

        // Получение списка сотрудников из CSV
        List<Employee> employees = parseCSV(csvFileName);

        // Преобразование списка в JSON
        String json = listToJson(employees);

        // Запись JSON в файл
        writeString("data.json", json);

        // XML файл для чтения
        String xmlFileName = "data.xml";

        // Получение списка сотрудников из XML
        List<Employee> employeesFromXML = parseXML(xmlFileName);

        // Преобразование списка из XML в JSON
        String jsonFromXML = listToJson(employeesFromXML);

        // Запись JSON из XML в файл
        writeString("data2.json", jsonFromXML);
    }

    public static List<Employee> parseCSV(String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            String[] memberFieldsToBindTo = {"id", "firstName", "lastName", "country", "age"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withType(Employee.class)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> parseXML(String xmlFileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            // Создание фабрики и построителя для XML документа
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Получение XML документа
            Document document = builder.parse(new File(xmlFileName));

            // Получение корневого элемента
            Element rootElement = document.getDocumentElement();

            // Получение списка сотрудников
            NodeList employeeNodes = rootElement.getElementsByTagName("employee");

            for (int i = 0; i < employeeNodes.getLength(); i++) {
                Node employeeNode = employeeNodes.item(i);
                if (employeeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element employeeElement = (Element) employeeNode;

                    long id = Long.parseLong(employeeElement.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = employeeElement.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employeeElement.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employeeElement.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(employeeElement.getElementsByTagName("age").item(0).getTextContent());

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }

    public static void writeString(String fileName, String content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

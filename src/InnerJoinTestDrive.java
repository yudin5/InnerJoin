import java.io.*;
import java.util.*;

/**
 * Класс для решения задачи про Inner JOIN с помощью ArrayList, HashMap, SortedLinkedList.
 * @author Виталий Юдин
 */
public class InnerJoinTestDrive {
    public static void main(String[] args) {
        // Проверяем аргументы
        if (args[0] == null || args[1] == null || args[2] == null || args.length != 3) {
            System.out.println("Введите корректные пути имен файлов");
        } else {
            String tableAfileName = args[0];
            String tableBfileName = args[1];
            String joinTableFileName = args[2];
            int yy;

            try {
                ArrayList<Record> tableA = readFile(tableAfileName);
                ArrayList<Record> tableB = readFile(tableBfileName);

                // ARRAY LIST
                // Подаем на вход 2 ArrayList
                ArrayList<String> arrayListResult = arrayListInnerJoin(tableA, tableB);

                // SORTED LINKED LIST
                Collections.sort(tableA);
                Collections.sort(tableB);
                LinkedList<Record> linkedTableA = new LinkedList<>();
                linkedTableA.addAll(tableA); // Сформировали сортированный LinkedList
                LinkedList<Record> linkedTableB = new LinkedList<>();
                linkedTableB.addAll(tableB); // Сформировали сортированный LinkedList
                // Подаем на вход 2 LinkedList
                ArrayList<String> linkedListResult = linkedListInnerJoin(linkedTableA, linkedTableB);

                // HASH MAP
                HashMap<Integer, ArrayList<String>> hashTableA = makeHashTable(tableA); // Сформировали HashMap A
                HashMap<Integer, ArrayList<String>> hashTableB = makeHashTable(tableB); // Сформировали HashMap B
                // Подаем на вход 2 HashMap
                ArrayList<String> hashMapResult = hashMapInnerJoin(hashTableA, hashTableB);

                // Выводим результаты
                writeToFile(arrayListResult, linkedListResult, hashMapResult, joinTableFileName);
                System.out.println("Успешно. Проверьте файл с результатом - " + args[2]);

            } catch (IOException ex) {
                System.out.println("Ошибка ввода/вывода файла");
                //ex.printStackTrace();
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка в числовом представлении данных");
            }
        }
    }

    /**
     * INNER JOIN на основе ArrayList
     *
     * @param tableA Таблица А
     * @param tableB Таблица В
     * @return Возвращает результат Inner Join в виде списка
     */
    public static ArrayList<String> arrayListInnerJoin(ArrayList<Record> tableA, ArrayList<Record> tableB) {
        ArrayList<String> result = new ArrayList<>();
        for (Record recordFromB : tableB) {
            for (Record recordFromA : tableA) {
                if (recordFromA.number == recordFromB.number) {
                    result.add(recordFromA.number + " \t" + recordFromA.string + " \t" + recordFromB.string);
                }
            }
        }
        return result;
    }

    /**
     * INNER JOIN на основе отсортированного LinkedList
     *
     * @param tableA Таблица А
     * @param tableB Таблица В
     * @return Возвращает результат Inner Join в виде списка
     */
    public static ArrayList<String> linkedListInnerJoin(LinkedList<Record> tableA, LinkedList<Record> tableB) {

        ArrayList<String> result = new ArrayList<>();
        ListIterator<Record> tableAiter = tableA.listIterator();
        ListIterator<Record> tableBiter = tableB.listIterator();
        Record tableArecord = tableAiter.next();
        Record tableBrecord = tableBiter.next();

        while (true) {
            // Сравнили ID
            int diff = tableArecord.number - tableBrecord.number;

            // Если в табл.А меньше значение, то двигаем указатель дальше по списку А
            if (diff < 0) {
                if (tableAiter.hasNext()) {
                    tableArecord = tableAiter.next();
                } else break; // Конец списка А, выходим, дальше не проверяем

            // Если в табл.А больше значение, то двигаем указатель таблицы В
            } else if (diff > 0) {
                if (tableBiter.hasNext()) {
                    tableBrecord = tableBiter.next();
                } else break; // Конец списка В, выходим, дальше не проверяем

            } else { // Если равны, проверяем список на наличие повторений по ключу
                Iterator<Record> miniIterB = tableB.listIterator(tableBiter.nextIndex());
                ArrayList<Record> miniList = new ArrayList<>();
                miniList.add(tableBrecord);
                // Проходимся по мини-списку в таблице В, проверяем есть ли дупликаты
                while (miniIterB.hasNext()) {
                    Record miniNext = miniIterB.next();
                    if (miniNext.number == tableArecord.number) {
                        miniList.add(miniNext); // Если значения совпадают, то добавляем в список
                    } else break; // Если значения не совпадают, прерываем цикл, дальше идти не нужно
                }
                // Теперь пишем все значения в результат
                for (Record r : miniList) {
                    result.add(tableArecord.number +" \t"+ tableArecord.string +" \t"+ r.string);
                }
                if (tableAiter.hasNext()) { // Двигаем указатель дальше по списку А
                    tableArecord = tableAiter.next();
                } else {
                    break; // Если это был последний элемент списка А, то выходим из цикла полностью
                }
            }
        }
        return result;
    }

    /**
     * INNER JOIN на основе HASH MAP
     *
     * @param hashTableA Таблица А
     * @param hashTableB Таблица В
     * @return Возвращает результат Inner Join в виде списка
     */
    public static ArrayList<String> hashMapInnerJoin(HashMap<Integer, ArrayList<String>> hashTableA, HashMap<Integer, ArrayList<String>> hashTableB) {
        ArrayList<String> result = new ArrayList<>();
        for (int id : hashTableA.keySet()) {
            if (hashTableB.containsKey(id)) { // Ключи совпадают, записываем списки значений
                for (String valueA : hashTableA.get(id)) {
                    for (String valueB : hashTableB.get(id)) {
                        result.add(id +" \t"+ valueA +" \t"+ valueB);
                    }
                }
            } // Совпадение ключей не обнаружено, идем дальше
        }
        return result;
    }

    static HashMap<Integer, ArrayList<String>> makeHashTable(ArrayList<Record> list) {
        HashMap<Integer, ArrayList<String>> result = new HashMap<>();
        for (Record r : list) {
            if (result.containsKey(r.number)) {
                result.get(r.number).add(r.string);
            } else {
                result.put(r.number, new ArrayList<>());
                result.get(r.number).add(r.string);
            }
        }
        return result;
    }

    /**
     * Читает файл, полученный в качестве параметра и формируем на его основе таблицу
     *
     * @param fileName Имя файла
     * @return Таблицу в виду списка
     * @throws IOException           При ошибках в исходном файле
     * @throws NumberFormatException При ошибках в указании числа
     */
    static ArrayList<Record> readFile(String fileName) throws IOException {
        // Создаем таблицу
        ArrayList<Record> table = new ArrayList<>();
        // Проходимся по файлам, считываем таблицы
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        while (fileReader.ready()) {
            // Создаем запись
            Record record = new Record();
            // Читаем файл
            String nextLine = fileReader.readLine();
            if (nextLine != null && nextLine.trim().length() != 0) {
                String[] data = nextLine.split(",");
                if (data.length != 2) { // Проверка на количество элементов (должно быть 2)
                    System.out.println("Неверный формат данных.");
                    System.out.println("Данные должны быть указаны в формате <<Число, Строка>>");
                    System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                    throw new IOException();
                }

                // Очередное число
                record.number = Integer.parseInt(data[0].trim());

                // Очередная строка
                String nextString = data[1].trim();
                if (nextString.equals(null) || nextString.trim().length() == 0) {
                    System.out.println("Отсутствует строка");
                    System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                    throw new IOException();

                }
                try {
                    Integer.parseInt(nextString);
                    System.out.println("Второй аргумент - число. Должна быть строка.");
                    throw new IOException();
                } catch (NumberFormatException ex) {
                    //System.out.println("Данные прошли проверку");
                }
                record.string = nextString;
            }
            // Добавляем в таблицу
            table.add(record);
        }
        return table;
    }

    /**
     * Печатает результат в выходной файл
     *
     * @param fileName Имя файла
     * @throws IOException При ошибках в исходном файле
     */
    static void writeToFile( // подаем на вход все результаты
                             ArrayList<String> arrayListResult,
                             ArrayList<String> linkedListResult,
                             ArrayList<String> hashMapResult,
                             String fileName) throws IOException {
        // Формируем выходной файл
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
        fileWriter.write("ARRAY LIST\r\n");
        fileWriter.write("ID\tA.Value\tB.Value\r\n");
        for (String line : arrayListResult) {
            fileWriter.write(line + "\r\n");
        }
        fileWriter.write("======================\r\n");

        fileWriter.write("SORTED LINKED LIST\r\n");
        fileWriter.write("ID\tA.Value\tB.Value\r\n");
        for (String line : linkedListResult) {
            fileWriter.write(line + "\r\n");
        }
        fileWriter.write("======================\r\n");

        fileWriter.write("HASH MAP\r\n");
        fileWriter.write("ID\tA.Value\tB.Value\r\n");
        for (String line : hashMapResult) {
            fileWriter.write(line + "\r\n");
        }
        fileWriter.close();
    }
}
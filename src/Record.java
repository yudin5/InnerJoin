public class Record implements Comparable<Record> {
    // Оставил открытыми, чтобы не загромождать код. В настоящей программе будут приватными, а доступ через геттеры и сеттеры
    public int number;
    public String string;

    public String toString() {
        return (number + " " + string + "\n");
    }

    // Компаратор для сортировки
    @Override
    public int compareTo(Record o) {
        return number - o.number;
    }
}

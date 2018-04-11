package tsn.jdbc.derby;

import java.io.File;
import org.apache.derby.drda.NetworkServerControl;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

public class App1 {

    public static void main(String[] args) {
        try {
            // Имя подкаталога с базой данных
            String name_base_dir = "MyBase";

            // Определение каталога запуска программы (текущего каталога)
            String prog_dir = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath()
                    + System.getProperty("file.separator");

            // Установка каталога БД
            System.setProperty("derby.system.home", prog_dir + name_base_dir);

            // Запуск сервера
            NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
            server.start(null);

            // Загрузка драйвера JavaDB
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            // Создание свойств соединения с базой данных
            Properties authorization = new Properties();
            authorization.setProperty("user", "tsn"); // Зададим имя пользователя БД
            authorization.setProperty("password", "tsn"); // Зададим пароль доступа в БД

            // Создание соединения с базой данных
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/disks", authorization);

            // Создание оператора доступа к базе данных
            java.sql.Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            // Выполнение запроса к базе данных
            statement.execute("select ID, NAME_DISK, PRICE_PUR, PRICE_SEL from disk_1"); // Выборка таблицы
            // Получение набора данных
            ResultSet table = statement.getResultSet();

            table.first(); // Выведем имена полей
            for (int j = 1; j <= table.getMetaData().getColumnCount(); j++) {
                System.out.print(table.getMetaData().getColumnName(j) + "\t\t");
            }
            System.out.println();

            table.beforeFirst(); // Выведем записи таблицы
            while (table.next()) {
                for (int j = 1; j <= table.getMetaData().getColumnCount(); j++) {
                    System.out.print(table.getString(j) + "\t\t");
                }
                System.out.println();
            }

            if (table != null) { table.close(); } // Закрытие набора данных
            if (statement != null) { statement.close(); } // Закрытие базы данных
            if (connection != null) { connection.close(); } // Отключение от базы данных
            server.shutdown(); // Выключение сервера
        } catch (Exception e) {
            System.err.println("Error accessing database!");
        }
    }
}

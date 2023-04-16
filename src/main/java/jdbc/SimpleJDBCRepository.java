package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ?, id = ?;";
    private static final String deleteUser = "DELETE FROM myusers WHERE lastname = ?;";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?;";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?;";
    private static final String findAllUserSQL = "SELECT * FROM myusers;";

    public Long createUser(User user) throws IOException, SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(createUserSQL);
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        ps.executeUpdate();
        connection.close();
        return user.getId();
    }

    public User findUserById(Long userId) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByIdSQL.concat(userId.toString()).concat(";"));
        ResultSet resultSet = ps.executeQuery();
        User user = new User(resultSet.getLong("id"), resultSet.getString("firstName"),
                resultSet.getString("lastName"), resultSet.getInt("age"));
        connection.close();
        resultSet.close();
        return user;
    }

    public User findUserByName(String userName) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByNameSQL.concat(userName).concat(";"));
        ResultSet resultSet = ps.executeQuery();
        User user = new User(resultSet.getLong("id"), resultSet.getString("firstName"),
                resultSet.getString("lastName"), resultSet.getInt("age"));
        connection.close();
        resultSet.close();
        return user;
    }

    public List<User> findAllUser() throws IOException, SQLException {
        List<User> users = new ArrayList<>();
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findAllUserSQL);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            User user = new User(resultSet.getLong("id"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getInt("age"));
            users.add(user);
        }
        connection.close();
        resultSet.close();
        return users;
    }

    public User updateUser(User user) throws IOException, SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(updateUserSQL);
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        ps.setLong(4, user.getId());
        ps.executeUpdate();
        connection.close();
        return findUserById(user.getId());
    }

    private void deleteUser(Long userId) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(deleteUser);
        ps.setLong(1, userId);
        ps.executeUpdate();
        connection.close();
    }
}

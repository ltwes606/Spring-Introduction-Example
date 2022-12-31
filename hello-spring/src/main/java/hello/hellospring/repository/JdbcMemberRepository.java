package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?);";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, member.getName());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                member.setId(resultSet.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }

        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            List<Member> members = new ArrayList<>();

            while (resultSet.next()) {
                Member member = new Member();
                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                members.add(member);
            }
            return members;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    private void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (connection != null) {
                close(connection);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void close(Connection connection) {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }
}

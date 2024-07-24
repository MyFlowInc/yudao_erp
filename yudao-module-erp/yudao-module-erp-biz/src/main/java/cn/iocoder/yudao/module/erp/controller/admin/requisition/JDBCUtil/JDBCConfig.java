package cn.iocoder.yudao.module.erp.controller.admin.requisition.JDBCUtil;

import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.ErpAiluoSplProjectVO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCConfig {
    // 数据库连接信息
    private static final String url = "jdbc:mysql://rm-uf6d0o1236tchk4pg7o.mysql.rds.aliyuncs.com:3306/pear-admin-pro?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai";
    private static final String username = "flow";
    private static final String password = "@zjyfrank5903143";

    // 执行 SQL 查询并返回结果集
    public static List<ErpAiluoSplProjectVO> executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 连接数据库
            conn = DriverManager.getConnection(url, username, password);

            // 准备 SQL 语句
            stmt = conn.prepareStatement(sql);

            // 设置 SQL 语句中的参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // 执行查询
            rs = stmt.executeQuery();

            // 将 ResultSet 映射为对象列表
            List<ErpAiluoSplProjectVO> projects = mapResultSetToErpAiluoSplProjects(rs);
            return projects;

        } catch (SQLException e) {
            // 异常处理
            throw new SQLException("Failed to execute query: " + e.getMessage(), e);
        } finally {
            // 关闭资源
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    public static List<ErpAiluoSplProjectVO> mapResultSetToErpAiluoSplProjects(ResultSet rs) throws SQLException {
        List<ErpAiluoSplProjectVO> projects = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");

            ErpAiluoSplProjectVO project = new ErpAiluoSplProjectVO();
            project.setId((long) id);
            project.setName(name);
            projects.add(project);
        }
        return projects;
    }

}

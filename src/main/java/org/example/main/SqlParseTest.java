package org.example.main;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/21 14:58
 */
public class SqlParseTest {




    public static void main(String[] args) {
        String sql = "select * from user";
        //        String sql = "select * from user u1, user02 u2 where id=1 and name='ming' and age=25 group by u1.uid limit 1,200 order by u1.ctime, u2.abce";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
        final SelectPrintVisitor selectPrintVisitor = new SelectPrintVisitor();
        for (SQLStatement stmt : stmtList) {
//            stmt.accept(visitor);
            stmt.accept(selectPrintVisitor);
        }


        final Map<String, SQLObject> aliasMap = selectPrintVisitor.getAliasMap();
        aliasMap.forEach((k,v)->{
            System.out.println("k:" + k + " v:" + v);
        });


//        String sql = "select * from user u1, user02 u2 where id=1 and name='ming' and age=25 group by u1.uid limit 1,200 order by u1.ctime, u2.abce";
//
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
//
//        System.out.println(stmtList.size());
//        final SQLStatement sqlStatement = stmtList.get(0);
//
//        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
//        sqlStatement.accept(statVisitor);
//
//        System.out.println(statVisitor.getColumns()); // [t_user.name, t_user.age, t_user.id]
//        System.out.println(statVisitor.getTables()); // {t_user=Select}
//        System.out.println(statVisitor.getConditions()); // [t_user.id = 1]

//        String sql = "select name, age from t_user where id = 1";

//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
//        SQLStatement stmt = stmtList.get(0);
//
//        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
//        stmt.accept(statVisitor);
//
//        System.out.println("group by:" + statVisitor.getGroupByColumns());
//        System.out.println("columns:" + statVisitor.getColumns()); // [t_user.name, t_user.age, t_user.id]
//        System.out.println("tables:" + statVisitor.getTables()); // {t_user=Select}
//        System.out.println("where:" + statVisitor.getConditions()); // [t_user.id = 1]
//        System.out.println("order by:" + statVisitor.getOrderByColumns()); // [t_user.id = 1]
//        System.out.println("order by:" + statVisitor.limi);
//        // 新建 MySQL Parser
//        SQLStatementParser parser = new MySqlStatementParser(sql);
//
//        // 使用Parser解析生成AST，这里SQLStatement就是AST
//        SQLStatement sqlStatement = parser.parseStatement();
//
//        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
//        sqlStatement.accept(visitor);
//
//        System.out.println("getTables:" + visitor.getTables());
//        System.out.println("getParameters:" + visitor.getParameters());
//        System.out.println("getOrderByColumns:" + visitor.getOrderByColumns());
//        System.out.println("getGroupByColumns:" + visitor.getGroupByColumns());
//        System.out.println("---------------------------------------------------------------------------");
//
//        // 使用select访问者进行select的关键信息打印
//        SelectPrintVisitor selectPrintVisitor = new SelectPrintVisitor();
//        sqlStatement.accept(selectPrintVisitor);
//
//        System.out.println("---------------------------------------------------------------------------");
//        // 最终sql输出
//        StringWriter out = new StringWriter();
//        TableNameVisitor outputVisitor = new TableNameVisitor(out);
//        sqlStatement.accept(outputVisitor);
//        System.out.println(out.toString());
    }

    public static class ExportTableAliasVisitor extends MySqlASTVisitorAdapter {
        private Map<String, SQLTableSource> aliasMap = new HashMap<String, SQLTableSource>();
        @Override
        public boolean visit(SQLExprTableSource x) {
            String alias = x.getAlias();
            final List<SQLName> columns = x.getColumns();
            return true;
        }

        public Map<String, SQLTableSource> getAliasMap() {
            return aliasMap;
        }
    }

}

/**
 * 查询语句访问者
 *
 * @author xiezhengchao
 * @since 2018/6/1 12:08
 */
class SelectPrintVisitor extends SQLASTVisitorAdapter {
    private Map<String, SQLObject> aliasMap = new HashMap<>();

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        List<SQLSelectItem> selectItemList = x.getSelectList();
        selectItemList.forEach(selectItem -> {
            System.out.println("attr:" + selectItem.getAttributes());
            System.out.println("expr:" + SQLUtils.toMySqlString(selectItem.getExpr()));
        });

        aliasMap.put("table", x.getFrom());
        aliasMap.put("where", x.getWhere());
        aliasMap.put("orderBy", x.getOrderBy());
        aliasMap.put("limit", x.getLimit());
        return true;
    }

    public Map<String, SQLObject> getAliasMap() {
        return aliasMap;
    }
}

/**
 * 数据库表名访问者
 *
 * @author xiezhengchao
 * @since 2018/6/1 11:52
 */
class TableNameVisitor extends MySqlOutputVisitor {

    public TableNameVisitor(Appendable appender) {
        super(appender);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        SQLName table = (SQLName) x.getExpr();
        String tableName = table.getSimpleName();

        // 改写tableName
        print0("new_" + tableName.toUpperCase());

        return true;
    }

}

// 自定义访问者
class SQLCustomedVisitor extends SQLASTVisitorAdapter {

    protected boolean hasLimit = false;

    @Override
    public boolean visit(SQLLimit x) {
        System.out.println(x.getRowCount());
        hasLimit = true;
        return false;
    }

    public boolean isHasLimit() {
        return hasLimit;
    }
}

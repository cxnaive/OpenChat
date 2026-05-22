package cn.handyplus.lib.db;

class DbConstant {
   protected static final String ID_FIELD = "id";
   protected static final int BATCH_SIZE = 500;
   protected static final String SELECT = "SELECT ";
   protected static final String INSERT = "INSERT INTO ";
   protected static final String UPDATE = "UPDATE ";
   protected static final String DELETE = "DELETE FROM ";
   protected static final String NOT_NULL = "NOT NULL";
   protected static final String AUTO_INCREMENT = " AUTO_INCREMENT";
   protected static final String DEFAULT = " DEFAULT '%s'";
   protected static final String COMMENT = " COMMENT '%s'";
   protected static final String SET = " SET ";
   protected static final String FROM = " FROM ";
   protected static final String VALUES = " VALUES ";
   protected static final String QUESTION_MARK = "?";
   protected static final String EQUALS = " = ";
   protected static final String SUBTRACT = " - ";
   protected static final String ADD = " + ";
   protected static final String POINT = "`";
   protected static final String TRANSFER = "'";
   protected static final String LEFT_BRACKET = "(";
   protected static final String RIGHT_BRACKET = ")";
   protected static final String PERCENT_SIGN = "%";
   protected static final String COMMA = " , ";
   protected static final String COUNT = "COUNT(*)";
   protected static final String COUNT_DISTINCT = "COUNT(DISTINCT `%s`)";
   protected static final String DEFAULT_WHERE = " where 1 = 1";
   protected static final String MYSQL_RAND = " ORDER BY RAND()";
   protected static final String SQLITE_RAND = " ORDER BY RANDOM()";
   protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (`id` INTEGER (11) AUTO_INCREMENT,PRIMARY KEY (`id`)) CHARACTER SET = utf8mb4 ENGINE=INNODB;";
   protected static final String TABLE_COMMENT = "ALTER TABLE `%s` COMMENT '%s';";
   protected static final String TABLE_INFO = "SELECT column_name FROM information_schema.COLUMNS WHERE table_name = '%s' AND table_schema = '%s';";
   protected static final String ADD_COLUMN = "ALTER TABLE `%s` ADD `%s` %s(%s) %s;";
   protected static final String ADD_COLUMN_COMMENT = "ALTER TABLE `%s` MODIFY `%s` %s(%s) %s;";
   protected static final String SQLITE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` ( `id` INTEGER PRIMARY KEY AUTOINCREMENT);";
   protected static final String SQLITE_TABLE_INFO = "PRAGMA table_info ( %s )";
   protected static final String SQLITE_ADD_COLUMN = "ALTER TABLE '%s' ADD '%s' %s(%s) %s;";
   protected static final String SHOW_INDEX = "SHOW INDEX FROM %s;";
   protected static final String ADD_INDEX = "ALTER TABLE %s ADD INDEX %s (%s);";
   protected static final Integer FIELD_TEXT_LENGTH = 16383;

   private DbConstant() {
   }
}

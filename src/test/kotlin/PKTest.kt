import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

object TestTableId : IdTable<String>("test_table_id") {
    val referenceNumber = varchar("reference_number", 30)
    override val id = referenceNumber.entityId()
    override val primaryKey = PrimaryKey(id)
}

object TestTableProp : IdTable<String>("test_table_prop") {
    val referenceNumber = varchar("reference_number", 30)
    override val id = referenceNumber.entityId()
    override val primaryKey = PrimaryKey(referenceNumber)
}

class PKTest {

    val db by lazy { Database.connect("jdbc:h2:mem:test") }

    @Test
    fun `Creates primary key derived from entity ID`() {
        transaction(db) {
            assertThat(SchemaUtils.createStatements(TestTableId).firstOrNull()).isEqualTo(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE_ID (REFERENCE_NUMBER VARCHAR(30) PRIMARY KEY)"
            )
        }
    }

    @Test
    fun `Creates primary key derived from property`() {
        transaction(db) {
            val stmt = SchemaUtils.createStatements(TestTableProp).firstOrNull()
            println(stmt)
            assertThat(stmt).isEqualTo(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE_PROP (REFERENCE_NUMBER VARCHAR(30) NOT NULL, CONSTRAINT pk_test_table_prop PRIMARY KEY (REFERENCE_NUMBER))"
            )
        }
    }
}
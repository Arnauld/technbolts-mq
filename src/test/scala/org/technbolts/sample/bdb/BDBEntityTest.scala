package org.technbolts.sample.bdb

import com.sleepycat.persist.model.Relationship._
import com.sleepycat.je.{Environment, EnvironmentConfig}
import java.io.File
import com.sleepycat.persist._
import evolve.{EvolveConfig, Conversion, Converter, Mutations}
import model.{EntityModel, SecondaryKey, PrimaryKey, Entity}
import raw.{RawType, RawObject}
import org.junit.{Ignore, After, Test, Before}

class PersonV0Conversion extends Conversion {

  var personType:RawType = null

  override def equals(that: Any) = that match {
   case other: PersonV0Conversion => true
   case _ => false
 }

  def convert(p1: Any) = {
    val person = p1.asInstanceOf[RawObject]
    val values = person.getValues
    values.put("phone", "01234")
    new RawObject(personType, values, person.getSuper()).asInstanceOf[Object]
  }

  def initialize(entityModel: EntityModel) = {
    personType = entityModel.getRawType(classOf[Person].getName)
  }
}

/**
 * From :
 *  http://forums.oracle.com/forums/thread.jspa?threadID=594269&tstart=15
 *
 * Simple example of using Berkeley DB Java Edition (JE) with Scala.  The JE
 * Direct Persistence Layer (DPL) is used in this example, which requires Java
 * 1.5, so the scalac -target:jvm-1.5 option is required when compiling.  The
 * -Ygenerics option must also be used because DPL generics are used in this
 * example.
 *
 *  scalac -Ygenerics -target:jvm-1.5 -cp je-x.y.z.jar ScalaPersonExample.scala
 *
 * To run the example:
 *
 *  mkdir ./tmp
 *  scala -cp ".;je-x.y.z.jar" ScalaPersonExample
 *
 * Note that classOf[java.lang.String] and classOf[java.lang.Long] are used
 * rather than classOf[String] and classOf[Long].  The latter use the Scala
 * types rather than the Java types and cause run-time errors.
 */

/**
 * A persistent Entity is defined using DPL annotations.
 */
@Entity{val version=2}
class Person(nameParam: String, addressParam: String) {
  @PrimaryKey {val sequence = "ID"}
  var id: long = 0

  @SecondaryKey {val relate = ONE_TO_ONE}
  var name: String = nameParam

  var address: String = addressParam

  var phone:String = "-"

  private def this() = this (null, null) // default ctor for unmarshalling

  override def toString = "Person: " + id + ' ' + name + ' ' + address + ' ' + phone
}

@Ignore
class BDBEntityTest {
  var envConfig: EnvironmentConfig = null
  var env: Environment = null
  var store: EntityStore = null
  var primaryIndex: PrimaryIndex[java.lang.Long, Person] = null
  var secondaryIndex: SecondaryIndex[java.lang.String, java.lang.Long, Person] = null

  @Before
  def setUp(): Unit = {
    /* Open the JE Environment. */
    envConfig = new EnvironmentConfig()
    envConfig.setAllowCreate(true)
    envConfig.setTransactional(true)

    val root = new File("./target/bdb")
    if(!root.exists)
      root.mkdirs
    env = new Environment(root, envConfig)

    /* Mutations :) */
    val mutations = new Mutations()
    mutations.addConverter(new Converter(classOf[Person].getName, 1, new PersonV0Conversion()))

    /* Open the DPL Store. */
    val storeConfig = new StoreConfig()
    storeConfig.setAllowCreate(true)
    storeConfig.setTransactional(true)
    storeConfig.setMutations(mutations)
    store = new EntityStore(env, "ScalaPersonExample", storeConfig)

    println("Evolving")
    store.evolve(new EvolveConfig)
    println("Evolving done!")

    /* The PrimaryIndex maps the Long primary key to Person. */
    primaryIndex = store.getPrimaryIndex(classOf[java.lang.Long], classOf[Person])

    /* The SecondaryIndex maps the String secondary key to Person. */
    secondaryIndex = store.getSecondaryIndex(primaryIndex, classOf[java.lang.String], "name")
  }

  @After
  def tearDown(): Unit = {
    if(store!=null)
      store.close()
    if(env!=null)
      env.close()
  }

  def fillStore (): Unit = {
    /* Insert some entities if the primary index is empty. */
    val txn = env.beginTransaction(null, null)
    if (primaryIndex.get(txn, 1L, null) == null) {
        val person1 = new Person("Zola", "#1 Zola Street")
        val person2 = new Person("Abby", "#1 Abby Street")
        primaryIndex.put(txn, person1)
        primaryIndex.put(txn, person2)
        assert(person1.id == 1) // assigned from the ID sequence
        assert(person2.id == 2) // assigned from the ID sequence
        txn.commit()
        println("--- Entities were inserted ---")
    } else {
        txn.abort()
        println("--- Entities already exist ---")
    }
  }

  @Test
  def useCase():Unit = {
    fillStore

    /* Get entities by primary and secondary keys. */
    println("--- Get by primary key ---")
    println(primaryIndex.get(1L))
    println(primaryIndex.get(2L))
    assert(primaryIndex.get(3L) == null)
    println("--- Get by secondary key ---")
    println(secondaryIndex.get("Zola"))
    println(secondaryIndex.get("Abby"))
    assert(secondaryIndex.get("xxx") == null)

    /* Iterate entities in primary and secondary key order. */
    def printAll[T](cursor: EntityCursor[T]) {
        val person = cursor.next()
        if (person == null) {
            cursor.close()
        } else {
            println(person)
            printAll(cursor) // tail recursion
        }
    }
    println("--- Iterate by primary key ---")
    printAll(primaryIndex.entities())
    println("--- Iterate by secondary key ---")
    printAll(secondaryIndex.entities())
  }

}
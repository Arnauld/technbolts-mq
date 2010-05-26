package org.technbolts.sample.bdb

import java.io.File
import collection.mutable.HashMap
import com.sleepycat.je.{DatabaseConfig, Database, EnvironmentConfig, Environment}

/**
 * @author <a href="mailto:arnauld.loyer@gmail.com">Loyer Arnauld</a>
 * @version $Revision$
 */

object Env {
  def create(envHome: File, readOnly: Boolean) {
    new Env(createEnvironment(envHome, readOnly), readOnly)
  }

  def createEnvironment(envHome: File, readOnly: Boolean): Environment = {
    // Instantiate an environment configuration object
    val envConfig = new EnvironmentConfig()

    // Configure the environment for the read-only state as identified by
    // the readOnly parameter on this method call.
    envConfig.setReadOnly(readOnly)

    // If the environment is opened for write, then we want to be able to
    // create the environment if it does not exist.
    envConfig.setAllowCreate(!readOnly)

    // add transaction support if opened for write
    envConfig.setTransactional(!readOnly)

    // Instantiate the Environment. This opens it and also possibly
    // creates it.
    new Environment(envHome, envConfig);
  }
}

class Env(val environment: Environment, val readOnly: Boolean) {
  val databases = new HashMap[String, Db]

  def close(): Unit = {

    databases.values.foreach {db => db.close}

    if (environment != null)
      environment.close
  }

  def isTransactional = { !readOnly }

  def database(name: String): Db = {
    databases.get(name) match {
      case Some(db) => db
      case None =>
        // Open the database. Create it if it does not already exist.
        val dbConfig = new DatabaseConfig()
        /*
           If true, the database is created when it is opened. If false,
           the database open fails if the database does not exist. This
           property has no meaning if the database currently exists.
           Default is false.
         */
        dbConfig.setAllowCreate(!readOnly)

        dbConfig.setReadOnly(readOnly)
        dbConfig.setTransactional(isTransactional)

        val database = environment.openDatabase(null, name, dbConfig)
        val db = new Db(database)
        databases.put(name, db)
        db
    }
  }
}

class Db(val database: Database) {
  def close(): Unit = {
    if (database != null)
      database.close
  }
}
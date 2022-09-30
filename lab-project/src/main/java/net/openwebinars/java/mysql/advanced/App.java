package net.openwebinars.java.mysql.advanced;

import net.openwebinars.java.mysql.advanced.transactions.IsolationExample;
import net.openwebinars.java.mysql.advanced.transactions.SavePointExample;
import net.openwebinars.java.mysql.advanced.transactions.TransactionsExample;

import java.sql.Savepoint;

public class App {

    public static void main(String[] args) {

        // StoredProcedureExample storedProcedureExample = new StoredProcedureExample();
        // storedProcedureExample.test();

        //AutoCommitExample autoCommitExample = new AutoCommitExample();
        //autoCommitExample.test();

        //TransactionsExample transactionsExample = new TransactionsExample();
        //transactionsExample.transactionExample();

        //IsolationExample isolationExample = new IsolationExample();
        //isolationExample.test();

        SavePointExample savePointExample = new SavePointExample();
        savePointExample.test();

    }

}

// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import controller.TestErrorHandler;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;
  private TestErrorHandler errorHandler;



  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    errorHandler = new TestErrorHandler();
    controller = new ExpenseTrackerController(model, view, errorHandler);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }

    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }


    // New Test1 ： Add Transaction
   @Test
    public void testAddTransactionUpdatesJTable() {
        // Pre-condition: Check initial state of the JTable
        DefaultTableModel tableModel = (DefaultTableModel) view.getTransactionsTable().getModel();
        int initialRowCount = tableModel.getRowCount();

        // Execution: Add a transaction
        controller.addTransaction(50.00, "food");

        // Post-condition: Verify the JTable is updated correctly
        int expectedRowCount = initialRowCount + 2; // One for the transaction and one for the total

        assertEquals(expectedRowCount, tableModel.getRowCount());

        // Check the content of the new row
        int newRowIndex = initialRowCount; // Index of the new row
        assertEquals(50.0, Double.parseDouble(tableModel.getValueAt(newRowIndex, 1).toString()), 0.01);
        assertEquals( "food", tableModel.getValueAt(newRowIndex, 2).toString());
    }

    // New Test2 ： Invalid Input Handling
    @Test
    public void testAddTransactionWithInvalidInput() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            // Pre-condition: Check initial state of the model and JTable
            assertEquals(0, model.getTransactions().size());
            DefaultTableModel tableModel = (DefaultTableModel) view.getTransactionsTable().getModel();
            int initialRowCount = tableModel.getRowCount();
            double initialTotalCost = getTotalCost();

            // Attempt to add an invalid transaction
            double invalidAmount = 1500; // Assuming negative amount is invalid
            String invalidCategory = "";  // Assuming empty category is invalid
            assertFalse(controller.addTransaction(invalidAmount, invalidCategory));

            // Post-condition: Verify that the model and JTable remain unchanged
            assertEquals(0, model.getTransactions().size());
            assertEquals(initialRowCount, tableModel.getRowCount());
            assertEquals(initialTotalCost, getTotalCost(), 0.01);

            assertTrue("Error should be reported for invalid transaction input", errorHandler.isErrorReported());
        });
    }

    // New Test3 ： Filter by Amount
    @Test
    public void testFilterByAmount() {
        // Pre-condition: Add transactions with different amounts
        controller.addTransaction(50.0, "food");
        controller.addTransaction(75.0, "travel");
        controller.addTransaction(100.0, "entertainment");

        // Apply filter by amount
        double filterAmount = 75.0;
        controller.setFilter(new AmountFilter(filterAmount));
        controller.applyFilter();

        // Post-condition: Verify that only transactions matching the amount are highlighted
        int[] highlightedRows = view.getHighlightedRows();
        assertEquals(1, highlightedRows.length);
        int highlightedRowIndex = highlightedRows[0];
        Transaction highlightedTransaction = model.getTransactions().get(highlightedRowIndex);
        assertEquals( filterAmount, highlightedTransaction.getAmount(), 0.01);
    }

    // New Test4 ： Filter by Category
    @Test
    public void testFilterByCategory() {
        // Pre-condition: Add transactions with different categories
        controller.addTransaction(50.0, "food");
        controller.addTransaction(75.0, "travel");
        controller.addTransaction(100.0, "entertainment");

        // Apply filter by category
        String filterCategory = "food";
        controller.setFilter(new CategoryFilter(filterCategory));
        controller.applyFilter();

        // Post-condition: Verify that only transactions matching the category are highlighted
        int[] highlightedRows = view.getHighlightedRows();
        assertEquals(1, highlightedRows.length);
        int highlightedRowIndex = highlightedRows[0];
        Transaction highlightedTransaction = model.getTransactions().get(highlightedRowIndex);
        assertEquals(filterCategory, highlightedTransaction.getCategory());
    }

    // New Test5 ： Undo disallowed
    @Test
    public void testUndoDisallowedWhenEmpty() throws Exception {
        SwingUtilities.invokeAndWait(() -> {

            // Pre-condition: Clear any existing transactions to ensure an empty transaction list
            while (model.getTransactions().size() > 0) {
                controller.undoTransaction(0); // Assuming this method removes the transaction
            }

            // Confirm that the transaction list and the view's table model are empty
            assertTrue(model.getTransactions().isEmpty());
            assertEquals(0, view.getTableModel().getRowCount());

            // Perform the action: Attempt to undo a transaction when the list is empty
            JButton undoButton = view.getUndoTransactionBtn();
            undoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.undoTransaction(0);
                }
            });

            // Perform the action: Click the undo button
            undoButton.doClick();

            // Post-condition: Verify that the row count is still 0, indicating no change
            assertTrue("Error should be reported for undo with empty list", errorHandler.isErrorReported());
        });
    }

    // New Test6 ： Undo allowed
    @Test
    public void testUndoAllowed() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
        // Pre-condition: Add a transaction
        controller.addTransaction(50.0, "food");
        // Verify that the initial row count is 2， with total line
        assertEquals(2, view.getTableModel().getRowCount());

        // Perform the action: Click the undo button
        view.addUndoTransactionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undoTransaction(0);
            }
        });

        JButton undoButton = view.getUndoTransactionBtn();
        undoButton.doClick();

        // Post-condition: Verify that the row count is 1 with total line kept
        assertEquals(1, view.getTableModel().getRowCount());
        });}
}

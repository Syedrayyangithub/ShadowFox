package com.example.LibraryManagementSystem.model;

import java.time.LocalDate;

public class Borrowing {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Borrowing(int id, int userId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return "Borrowing ID: " + id +
                ", User ID: " + userId +
                ", Book ID: " + bookId +
                ", Borrow Date: " + borrowDate +
                ", Due Date: " + dueDate +
                (returnDate != null ? ", Return Date: " + returnDate : "");
    }
}
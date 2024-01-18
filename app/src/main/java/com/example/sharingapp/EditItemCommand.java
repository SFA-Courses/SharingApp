package com.example.sharingapp;

import java.util.concurrent.ExecutionException;

/**
 * Command used to edit pre-existing item
 */
public class EditItemCommand extends Command {
    private Item old_item;
    private Item new_item;

    public EditItemCommand(Item old_item, Item new_item) {
        this.old_item = old_item;
        this.new_item = new_item;
    }

    // Delete the old item remotely, save the new item remotely to server
    public void execute() {
        try {
            DatabaseManager.getInstance().addItem(this.new_item);
            super.setIsExecuted(true);
        } catch (Exception e) {
            e.printStackTrace();
            super.setIsExecuted(false);
        }
    }
}

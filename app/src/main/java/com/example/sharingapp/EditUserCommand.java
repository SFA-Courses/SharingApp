package com.example.sharingapp;

/**
 * Command used to edit pre-existing user
 */
public class EditUserCommand extends Command {

    private User old_user;
    private User new_user;

    public EditUserCommand (User old_user, User new_user){
        this.old_user = old_user;
        this.new_user = new_user;
    }

    // Delete the old user remotely, save the new user remotely to server
    public void execute() {
        try {
            DatabaseManager.getInstance().addUser(this.new_user);
            super.setIsExecuted(true);
        } catch (Exception e) {
            e.printStackTrace();
            super.setIsExecuted(false);
        }
    }
}

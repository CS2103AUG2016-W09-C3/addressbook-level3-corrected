package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.person.UniquePersonList.DuplicatePersonException;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;

import java.util.HashSet;
import java.util.Set;

/**
 * Edits a person's details in the address book, using index of the listing provided to specify target person.
 * User is suppose to list all person beforehand and use that index to specify target person.
 */

public class EditCommand extends Command{
    
    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Edits a person's details in the address book based on specified index. "
            + "Contact details can be marked private by prepending 'p' to the prefix.\n\t"
            + "Parameters:INDEX NAME [p]p/PHONE [p]e/EMAIL [p]a/ADDRESS  [t/TAG]...\n\t"
            + "Example: " + COMMAND_WORD + " " + 1
            + " John Doe p/98765432 e/johnd@gmail.com a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney";

    public static final String MESSAGE_SUCCESS = "Person edited: %1$s";
    public static final String MESSAGE_MISSING_PERSON = "This person does not exist in the address book";
    public static final String MESSAGE_DUPLICATE_PERSON = "This name already exists in the address book";
    public static final String MESSAGE_DUPLICATE_PERSON_ORIGINAL = "Failed to edit because new name already exists in address book, "
                                                                   + "specified person to edit is deleted. \n\t"
                                                                   + "Please re-add the specified person manually.";

    private final Person toAdd;
    
    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public EditCommand(String index,
                      String name,
                      String phone, boolean isPhonePrivate,
                      String email, boolean isEmailPrivate,
                      String address, boolean isAddressPrivate,
                      Set<String> tags) throws IllegalValueException {
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Person(
                new Name(name),
                new Phone(phone, isPhonePrivate),
                new Email(email, isEmailPrivate),
                new Address(address, isAddressPrivate),
                new UniqueTagList(tagSet)
        );
        
        this.setTargetIndex(Integer.parseInt(index));
    }
    
    public ReadOnlyPerson getPerson() {
        return toAdd;
    }

    @Override
    public CommandResult execute() {
        
        Person removed = null;
        
        try {
            final ReadOnlyPerson target = getTargetPerson();
            addressBook.removePerson(target);
            removed = (Person) target;
            addressBook.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, target));

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            if(removed != null){
                try {
                    addressBook.addPerson(removed);
                } catch (DuplicatePersonException e) {
                    return new CommandResult(MESSAGE_DUPLICATE_PERSON_ORIGINAL);
                }
            }
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }

}

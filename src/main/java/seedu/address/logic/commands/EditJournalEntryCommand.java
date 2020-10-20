package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE_AND_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ENTRIES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.journal.Date;
import seedu.address.model.journal.Description;
import seedu.address.model.journal.Entry;
import seedu.address.model.journal.Title;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.tag.Tag;

public class EditJournalEntryCommand extends Command {

    public static final String COMMAND_WORD = "editj";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "TITLE] "
            + "[" + PREFIX_DATE_AND_TIME + "DATE_AND_TIME] "
            + "[" + PREFIX_DESCRIPTION + "DESCRIPTION] "
            + "[" + PREFIX_CONTACT + "CONTACT_NAME] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + "Meet with client Robert "
            + PREFIX_DATE_AND_TIME + "2020-10-10 10:00";

    public static final String MESSAGE_EDIT_ENTRY_SUCCESS = "Edited Journal "
            + "Entry: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to "
            + "edit must be provided.";
    public static final String MESSAGE_DUPLICATE_ENTRY = "This person already"
            + " exists in the address book.";

    private final Index index;
    private final EditJournalEntryDescriptor editJournalEntryDescriptor;

    /**
     * @param index
     */
    public EditJournalEntryCommand(Index index,
                                   EditJournalEntryDescriptor editJournalEntryDescriptor) {
        requireNonNull(index);
        requireNonNull(editJournalEntryDescriptor);

        this.index = index;
        this.editJournalEntryDescriptor = editJournalEntryDescriptor;
    }

    private static Entry createEditedEntry(Entry entryToEdit,
                                           EditJournalEntryDescriptor editJournalEntryDescriptor) {
        assert entryToEdit != null;

        Title updatedTitle =
                editJournalEntryDescriptor.getTitle().orElse(entryToEdit.getTitle());
        Date updatedDate =
                editJournalEntryDescriptor.getDate().orElse(entryToEdit.getDate());
        Description updatedDescription =
                editJournalEntryDescriptor.getDescription().orElse(entryToEdit.getDescription());
        // TODO: figure how to add contact list
        ObservableList<Person> updatedPersonList =
                editJournalEntryDescriptor.getContactList().orElse(entryToEdit.getContactList());

        UniquePersonList updatedContactList = new UniquePersonList();
        updatedPersonList.forEach(updatedContactList::add);

        Set<Tag> updatedTags =
                editJournalEntryDescriptor.getTags().orElse(entryToEdit.getTags());

        return new Entry(
                updatedTitle,
                updatedDate,
                updatedDescription,
                updatedContactList,
                updatedTags
        );

    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Entry> lastShownList = model.getFilteredEntryList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(
                    Messages.MESSAGE_INVALID_ENTRY_DISPLAYED_INDEX);
        }
        Entry entryToEdit = lastShownList.get(index.getZeroBased());
        Entry editedEntry = createEditedEntry(entryToEdit,
                editJournalEntryDescriptor);
        if (!entryToEdit.isSameEntry(editedEntry) && model.hasEntry(editedEntry)) {
            throw new CommandException(MESSAGE_DUPLICATE_ENTRY);
        }
        model.setEntry(entryToEdit, editedEntry);
        model.updateFilteredEntryList(PREDICATE_SHOW_ALL_ENTRIES);

        return new CommandResult(
                String.format(MESSAGE_EDIT_ENTRY_SUCCESS, editedEntry));
    }

    public static class EditJournalEntryDescriptor {
        private Title title;
        private Date date;
        private Description description;
        private UniquePersonList contactList;
        private Set<Tag> tags;

        public EditJournalEntryDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} and {@code contactList} is used
         * internally.
         */
        public EditJournalEntryDescriptor(EditJournalEntryDescriptor toCopy) {
            setTitle(toCopy.title);
            setDate(toCopy.date);
            setDescription(toCopy.description);
            setContactList(toCopy.contactList);
            setTags(toCopy.tags);
        }

        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(
                    title,
                    date,
                    description,
                    tags,
                    contactList
            );
        }

        public Optional<Title> getTitle() {
            return Optional.ofNullable(title);
        }

        public void setTitle(Title title) {
            this.title = title;
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Optional<Description> getDescription() {
            return Optional.ofNullable(description);
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public Optional<ObservableList<Person>> getContactList() {
            return contactList.spliterator().getExactSizeIfKnown() > 0
                    ? Optional.of(contactList.asUnmodifiableObservableList())
                    : Optional.empty();
        }

        /**
         * Sets {@code contactList} to this object's {@code contactList}.
         * A defensive copy of {@code contactList} is made and used internally.
         */
        public void setContactList(UniquePersonList contactList) {
            this.contactList = new UniquePersonList();
            contactList.forEach(this.contactList::add);
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException} if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return tags != null
                    ? Optional.of(Collections.unmodifiableSet(tags))
                    : Optional.empty();
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditJournalEntryDescriptor)) {
                return false;
            }

            // state check
            EditJournalEntryDescriptor e = (EditJournalEntryDescriptor) other;

            return getTitle().equals(e.getTitle())
                    && getContactList().equals(e.getContactList())
                    && getDate().equals(e.getDate())
                    && getDescription().equals(e.getDescription())
                    && getTags().equals(e.getTags());

        }
    }
}
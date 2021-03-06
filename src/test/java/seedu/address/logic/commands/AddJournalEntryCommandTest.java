package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.parser.ValidCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliasMap;
import seedu.address.model.ReadOnlyJournal;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.journal.Entry;
import seedu.address.model.person.Person;
import seedu.address.testutil.EntryBuilder;

class AddJournalEntryCommandTest {
    /**
     * A default model stub that have all of the methods failing.
     */
    private static class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearJournalContacts() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyJournal getJournal() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setJournal(ReadOnlyJournal newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasName(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteEntry(Entry entry) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateJournalContacts(
                Person originalPerson, Person updatedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateAlias(Map<String, ValidCommand> map) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAliasMap getAliasMap() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setEntry(Entry target, Entry editedEntry) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasEntry(Entry entry) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addEntry(Entry entry) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Entry> getFilteredEntryList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getRecentPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFrequentPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredEntryList(Predicate<Entry> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single entry.
     */
    private static class ModelStubWithEntry extends ModelStub {
        private final Entry entry;

        ModelStubWithEntry(Entry entry) {
            requireNonNull(entry);
            this.entry = entry;
        }

        @Override
        public boolean hasEntry(Entry entry) {
            requireNonNull(entry);
            return this.entry.isSameEntry(entry);
        }
    }

    /**
     * A Model stub that always accept the entry being added.
     */
    private static class ModelStubAcceptingEntryAdded extends AddJournalEntryCommandTest.ModelStub {
        final ArrayList<Entry> entriesAdded = new ArrayList<>();

        @Override
        public boolean hasEntry(Entry entry) {
            requireNonNull(entry);
            return entriesAdded.stream().anyMatch(entry::isSameEntry);
        }

        @Override
        public void addEntry(Entry entry) {
            requireNonNull(entry);
            entriesAdded.add(entry);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {
        @Test
        @DisplayName("should throw NullPointerException if null entry is "
                + "passed into constructor")
        public void constructor_nullPerson_throwsNullPointerException() {
            assertThrows(NullPointerException.class, () ->
                    new AddJournalEntryCommand(null));
        }
    }

    @Nested
    @DisplayName("execute method")
    class Execute {
        @Test
        @DisplayName("should add entry successfully if entry is valid")
        public void execute_entryAcceptedByModel_addSuccessful()
                throws Exception {
            ModelStubAcceptingEntryAdded modelStub =
                    new ModelStubAcceptingEntryAdded();
            Entry validEntry = new EntryBuilder().build();

            CommandResult commandResult =
                    new AddJournalEntryCommand(validEntry).execute(modelStub);

            assertEquals(
                    String.format(AddJournalEntryCommand.MESSAGE_SUCCESS, validEntry),
                    commandResult.getFeedbackToUser()
            );
            assertEquals(Arrays.asList(validEntry), modelStub.entriesAdded);
        }

        @Nested
        @DisplayName("equals method")
        class Equals {
            private final Entry meeting = new EntryBuilder()
                    .withTitle("Meeting").build();
            private final Entry discussion = new EntryBuilder()
                    .withTitle("Discussion").build();
            private final AddJournalEntryCommand addMeetingCommand =
                    new AddJournalEntryCommand(meeting);
            private final AddJournalEntryCommand addDiscussionCommand =
                    new AddJournalEntryCommand(discussion);

            @Test
            @DisplayName("should return true if same object")
            public void equals_sameObject_true() {
                assertTrue(addMeetingCommand.equals(addMeetingCommand));
            }

            @Test
            @DisplayName("should return true if same values")
            public void equals_sameValues_true() {
                AddJournalEntryCommand addMeetingCommandCopy = new AddJournalEntryCommand(meeting);
                assertTrue(addMeetingCommand.equals(addMeetingCommandCopy));
            }

            @Test
            @DisplayName("should return false if different values")
            public void equals_differentValues_false() {
                assertFalse(addMeetingCommand.equals(1));
            }

            @Test
            @DisplayName("should return false if null")
            public void equals_null_false() {
                assertFalse(addMeetingCommand.equals(null));
            }

            @Test
            @DisplayName("should return false if different entry")
            public void equals_differentPerson_false() {
                assertFalse(addMeetingCommand.equals(addDiscussionCommand));
            }
        }
    }
}

package io.jenkins.plugins.analysis.warnings;

import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
import org.jvnet.localizer.LocaleProvider;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.parser.FindBugsParser;
import static edu.hm.hafner.analysis.parser.FindBugsParser.PriorityProperty.*;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisLabelProvider;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisTool;

import hudson.Extension;

/**
 * Provides a parser and customized messages for FindBugs.
 *
 * @author Ullrich Hafner
 */
public class FindBugs extends StaticAnalysisTool {
    private static final long serialVersionUID = 4692318309214830824L;
    static final String ID = "findbugs";

    private boolean useRankAsPriority;

    /** Creates a new instance of {@link FindBugs}. */
    @DataBoundConstructor
    public FindBugs() {
        super();
        // empty constructor required for stapler
    }

    @Override
    public boolean canScanConsoleLog() {
        return false;
    }

    public boolean getUseRankAsPriority() {
        return useRankAsPriority;
    }

    /**
     * If useRankAsPriority is {@code true}, then the FindBugs parser will use the rank when evaluation the priority.
     * Otherwise the priority of the FindBugs warning will be mapped.
     *
     * @param useRankAsPriority
     *         {@code true} to use the rank, {@code false} to use the
     */
    @DataBoundSetter
    public void setUseRankAsPriority(final boolean useRankAsPriority) {
        this.useRankAsPriority = useRankAsPriority;
    }

    @Override
    public FindBugsParser createParser() {
        return new FindBugsParser(RANK);
    }

    /** Provides the labels for the static analysis tool. */
    static class FindBugsLabelProvider extends IconLabelProvider {
        private final FindBugsMessages messages;

        /**
         * Creates a new {@link FindBugsLabelProvider} with the specified ID.
         *
         * @param messages
         *         the details messages
         * @param id
         *         the ID
         * @param name
         *         the name of the static analysis tool
         */
        FindBugsLabelProvider(final FindBugsMessages messages, final String id, final String name) {
            super(id, name, id);

            this.messages = messages;
        }

        @Override
        public String getDescription(final Issue issue) {
            return messages.getMessage(issue.getType(), LocaleProvider.getLocale());
        }
    }

    /** Descriptor for this static analysis tool. */
    @Symbol("findBugs")
    @Extension
    public static class FindBugsDescriptor extends StaticAnalysisToolDescriptor {
        private final FindBugsMessages messages = new FindBugsMessages();

        /** Creates the descriptor instance. */
        public FindBugsDescriptor() {
            this(ID);
        }

        /**
         * Creates the descriptor instance.
         *
         * @param id
         *         ID of the tool
         */
        public FindBugsDescriptor(final String id) {
            super(id);

            messages.initialize();
        }

        protected FindBugsMessages getMessages() {
            return messages;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.Warnings_FindBugs_ParserName();
        }

        @Override
        public StaticAnalysisLabelProvider getLabelProvider() {
            return new FindBugsLabelProvider(messages, getId(), getDisplayName());
        }

        @Override
        public String getPattern() {
            return "**/findbugsXml.xml";
        }
    }
}

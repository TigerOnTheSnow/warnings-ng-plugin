package io.jenkins.plugins.analysis.core.model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.AbstractParser;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Report;
import static io.jenkins.plugins.analysis.core.testutil.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link StaticAnalysisToolSuite}.
 *
 * @author Arne Schöntag
 */
class StaticAnalysisToolSuiteTest {
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final Function<String, String> IDENTITY = Function.identity();
    private static final Path FILE = mock(Path.class);

    @Test
    void shouldReturnEmptyReportIfSuiteIsEmpty() {
        TestStaticAnalysisToolSuite suite = new TestStaticAnalysisToolSuite();

        Report issues = suite.createParser().parse(FILE, ENCODING, IDENTITY);

        assertThat(issues).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnReportOfSingleParser() {
        AbstractParser parser = createParserStub();
        Report issues = createIssues(1);
        when(parser.parse(FILE, ENCODING, IDENTITY)).thenReturn(issues);

        TestStaticAnalysisToolSuite suite = new TestStaticAnalysisToolSuite(parser);

        Report compositeIssues = suite.createParser().parse(FILE, ENCODING, IDENTITY);

        assertThat(compositeIssues).isEqualTo(issues);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnAggregationOfTwoParsers() {
        AbstractParser firstParser = createParserStub();
        Report issuesFirstParser = createIssues(1);
        when(firstParser.parse(FILE, ENCODING, IDENTITY)).thenReturn(issuesFirstParser);

        AbstractParser secondParser = createParserStub();
        Report issuesSecondParser = createIssues(2);
        when(secondParser.parse(FILE, ENCODING, IDENTITY)).thenReturn(issuesSecondParser);

        TestStaticAnalysisToolSuite suite = new TestStaticAnalysisToolSuite(firstParser, secondParser);

        Report compositeIssues = suite.createParser().parse(FILE, ENCODING, IDENTITY);

        Report expected = new Report();
        expected.addAll(issuesFirstParser, issuesSecondParser);
        assertThat(compositeIssues).isEqualTo(expected);
    }

    private AbstractParser createParserStub() {
        AbstractParser stub = mock(AbstractParser.class);
        when(stub.accepts(any(), any())).thenReturn(true);
        
        return stub;
    }

    private Report createIssues(final int id) {
        Report issues = new Report();
        IssueBuilder issueBuilder = new IssueBuilder();
        issues.add(issueBuilder.setMessage(String.valueOf(id)).build());
        return issues;
    }

    /**
     * {@link StaticAnalysisToolSuite} to be used in the tests.
     */
    private class TestStaticAnalysisToolSuite extends StaticAnalysisToolSuite {
        private final Collection<? extends AbstractParser> parsers;

        TestStaticAnalysisToolSuite(final AbstractParser... parsers) {
            super();

            this.parsers = asList(parsers);
        }

        @Override
        protected Collection<? extends AbstractParser> getParsers() {
            return parsers;
        }
    }
}
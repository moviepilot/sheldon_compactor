package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.custom.SheldonConstants;
import com.moviepilot.sheldon.compactor.util.Progressor;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * PropertyContainerEvent
 *
 * Superclass of all events handled by the compactor
 *
 * @author stefanp
 * @since 05.08.12
 */
public class PropertyContainerEvent {

    public static enum Action {
        CREATE, UPDATE, DELETE
    }

    public Action action;

    public Exception failure;

    public static TMap<String, Object> makeNewPropertyMap(final Config config) {
        return new THashMap<String, Object>(config.getNumProps());
    }

    public static IndexEntry[] makeNewIndexEntrySet(final Config config) {
        final int numIndexEntries  = config.getNumIndexEntries();
        final IndexEntry[] entries = new IndexEntry[numIndexEntries];
        for (int i = 0; i < entries.length; i++)
            entries[i] = new IndexEntry(config);
        return entries;
    }

    /**
     * Properties read from the PropertyContainer
     */
    public final TMap<String, Object> props;

    /**
     * Id of the PropertyContainer
     */
    public long id;

    public IndexEntry[] indexEntries;

    public PropertyContainerEvent(final Config config) {
        props        = makeNewPropertyMap(config);
        indexEntries = makeNewIndexEntrySet(config);
        reset();
    }

    public boolean isOk() {
        return failure == null;
    }

    public TMap<String, Object> getProps() {
        return props.size() == 0 ? null : props;
    }

    public void clear(final Progressor progressor) {
        if (failure == null)
            progressor.tick("clean_ok");
        else {
            try {
                System.err.println(toString());
            }
            catch (final RuntimeException e) {
                // gulp
                System.err.println(e);
                e.printStackTrace(System.err);
            }
            progressor.tick("clean_failed");
        }
        try {
            reset();
        }
        catch (final RuntimeException e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            // This is too bad to continue as the event may remain in a dirty state
            System.exit(1);
        }
    }

    protected void reset() {
        props.clear();

        for (final IndexEntry indexEntry : indexEntries)
            indexEntry.clear();

        action  = null;
        failure = null;
    }

    @Override
    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        printWriter
                .append(getClass().getName())
                .append("{").append("id=").append(Long.toString(id));

        if (failure == null) {
            printWriter.append(", props=").append(strWrap(props))
                        .append(", action=").append(strWrap(action));
        }
        else {
            printWriter.append(", failure=").append(failure.toString());
            printWriter.append(", trace=\n");
            for (final StackTraceElement elem : failure.getStackTrace())
                printWriter.append(elem.toString());
            final Throwable cause = failure.getCause();
            printWriter.append(", cause=").append(cause.toString());
            printWriter.append(", cause_trace=\n");
            for (final StackTraceElement elem : cause.getStackTrace())
                printWriter.append(elem.toString());

        }
        printWriter.append("}");
        return stringWriter.toString();
    }

    private CharSequence strWrap(final Object o) {
        return o == null ? "null" : o.toString();
    }
}

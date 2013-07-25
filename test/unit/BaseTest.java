package unit;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public abstract class BaseTest
{
	@Rule
	public TestRule	watcher	= new TestWatcher()
							{
								@Override
								protected void starting(Description description)
								{
									System.out.println("BEGIN - " + description.getDisplayName());
									super.starting(description);
								}
								
								@Override
								protected void finished(Description description)
								{
									System.out.println("END - " + description.getDisplayName());
									super.finished(description);
								}
							};
}

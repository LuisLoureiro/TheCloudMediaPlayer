package unit;

import static org.junit.Assert.fail;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import play.db.jpa.JPA;
import play.libs.F.Callback0;
import play.libs.F.Function0;
import play.test.FakeApplication;

public abstract class BaseTest
{
	public static Map<String, String> settings = new HashMap<String, String>();
	public static String	firstUserId
						, secondUserId
						, firstUserEmail
						, firstPlaylistName
						, secondPlaylistName
						, firstContentId
						, secondContentId
						, firstContentProvider
						, secondContentProvider;
	public static int	firstPlaylistId
						, secondPlaylistId;
	public FakeApplication fakeApp;
	private RunningFakeAppWithReadOnlyTransaction fakeAppWithReadOnlyTransaction = new RunningFakeAppWithReadOnlyTransaction();
	private RunningFakeAppWithTransaction fakeAppWithTransaction = new RunningFakeAppWithTransaction();
	
	@Rule
	public TestRule	watcher	= new TestWatcher()
							{
								@Override
								protected void starting(Description description)
								{
									System.out.println("BEGIN - " + description.getDisplayName());
							        
							        fakeApp = fakeApplication(settings);
							        
									super.starting(description);
								}
								
								@Override
								protected void finished(Description description)
								{
									System.out.println("END - " + description.getDisplayName());
									super.finished(description);
								}
							};

	@BeforeClass
	public static void setUpBeforeClass()
	{
		settings.put("db.default.driver", "org.h2.Driver");
		settings.put("db.default.url", "jdbc:h2:mem:play;MODE=PostgreSQL");
		
		// Test with PostgreSQL database!
//        settings.put("db.default.driver", "org.postgresql.Driver");
//        settings.put("db.default.url", "jdbc:postgresql://localhost:5432/thecloudmediaplayertestdb");
//        settings.put("db.default.user", "test");
//        settings.put("db.default.password", "test");
//        settings.put("db.default.jndiName", "DefaultDS");
//        settings.put("jpa.default", "persistenceUnitTest");
		
		firstUserId = "firstUser";
		secondUserId = "secondUser";
		firstUserEmail = "first@mail.com";
		firstPlaylistId = 1;
		secondPlaylistId = 2;
		firstPlaylistName = "firstPlaylist";
		secondPlaylistName = "secondPlaylist";
		firstContentId = "firstContent";
		secondContentId = "secondContent";
		firstContentProvider = "dropbox";
		secondContentProvider = "soundcloud";
	}

	<R> void runFakeAppWithReadOnlyTransaction(Function0<R> func)
	{
		fakeAppWithReadOnlyTransaction.run(func);
	}

	<R> void runFakeAppWithTransaction(Callback0 callback)
	{
		fakeAppWithTransaction.run(callback);
	}
	
	private class RunningFakeAppWithReadOnlyTransaction
	{
		public <R> void run(final Function0<R> func)
		{
			running(fakeApp, new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						JPA.withTransaction("default", true, func);
					}
					catch(Throwable e)
					{
						e.printStackTrace();
						fail("Exception occurred: " + e.getMessage());
					}
				}
			});
		}
	}
	
	private class RunningFakeAppWithTransaction
	{
		public void run(final Callback0 callback)
		{
			running(fakeApp, new Runnable()
			{
				@Override
				public void run()
				{
					JPA.withTransaction(callback);
				}
			});
		}
	}
}

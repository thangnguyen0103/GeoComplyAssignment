package com.thangnguyen.geocomplyassigment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import android.os.Handler;
import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.thangnguyen.geocomplyassigment.domain.UseCaseScheduler;
import com.thangnguyen.geocomplyassigment.domain.repository.LinkRepository;
import com.thangnguyen.geocomplyassigment.domain.usecase.ParseDataUseCase;
import com.thangnguyen.geocomplyassigment.presentation.ui.MainViewModel;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    @Mock
    public LinkRepository repository;


    private MainViewModel mainViewModel;

    @Before
    public void setUp() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        UseCaseScheduler scheduler = new UseCaseScheduler(executor);
        ParseDataUseCase parseDataUseCase = new ParseDataUseCase(executor, repository);
        mainViewModel = new MainViewModel(scheduler, parseDataUseCase);
    }

    @Test
    public void mention_emptyInputParam_error() throws InterruptedException {
        mainViewModel.submitInput("");
        Assert.assertTrue(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getError()) instanceof IllegalArgumentException);
    }

    @Test
    public void mention_nullInputParam_error() throws InterruptedException {
        mainViewModel.submitInput(null);
        Assert.assertTrue(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getError()) instanceof IllegalArgumentException);
    }

    @Test
    public void mention_has1MentionValue_success() throws InterruptedException, JSONException {
        mainViewModel.submitInput("@Thang Nguyen");
        JSONObject result = toJsonObject(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getContent()));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getJSONArray("mentions"));
        Assert.assertTrue(result.isNull("links"));
        Assert.assertEquals("Thang", result.getJSONArray("mentions").get(0));
    }


    @Test
    public void mention_has2MentionsValue_success() throws InterruptedException, JSONException {
        mainViewModel.submitInput("@Thang @Nguyen???");
        JSONObject result = toJsonObject(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getContent()));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getJSONArray("mentions"));
        Assert.assertTrue(result.isNull("links"));
        Assert.assertEquals("Thang", result.getJSONArray("mentions").get(0));
        Assert.assertEquals("Nguyen", result.getJSONArray("mentions").get(1));
    }

    @Test
    public void mention_has0Values_success() throws InterruptedException {
        mainViewModel.submitInput("Thang Nguyen #@ABC");
        JSONObject result = toJsonObject(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getContent()));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isNull("links"));
        Assert.assertTrue(result.isNull("mentions"));
    }

    @Test
    public void link_has1Values_success() throws InterruptedException, JSONException {
        String url = "https://olympics.com/tokyo-2020/en";
        Mockito.when(repository.getTitle(Mockito.any())).thenReturn("title1");
        mainViewModel.submitInput(url);
        JSONObject result = toJsonObject(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getContent()));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isNull("mentions"));
        Assert.assertNotNull(result.getJSONArray("links"));
        Assert.assertEquals("title1", result.getJSONArray("links").getJSONObject(0).get("title"));
        Assert.assertEquals(url, result.getJSONArray("links").getJSONObject(0).get("url"));
    }

    @Test
    public void has_both_link_and_mention_value() throws InterruptedException, JSONException {
        String content = "@billgates do you know where is @elonmusk? Olympics 2020 is happening; https://olympics.com/tokyo-2020/en";
        Mockito.when(repository.getTitle(Mockito.any())).thenReturn("title2");
        mainViewModel.submitInput(content);
        JSONObject result = toJsonObject(LiveDataTestUtil.getOrAwaitValue(mainViewModel.getContent()));
        Assert.assertNotNull(result);

        Assert.assertNotNull(result.getJSONArray("mentions"));
        Assert.assertEquals("billgates", result.getJSONArray("mentions").get(0));
        Assert.assertEquals("elonmusk", result.getJSONArray("mentions").get(1));

        Assert.assertNotNull(result.getJSONArray("links"));
        Assert.assertEquals("title2", result.getJSONArray("links").getJSONObject(0).get("title"));
        Assert.assertEquals("https://olympics.com/tokyo-2020/en", result.getJSONArray("links").getJSONObject(0).get("url"));
    }


    private JSONObject toJsonObject(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            return null;
        }
    }
}
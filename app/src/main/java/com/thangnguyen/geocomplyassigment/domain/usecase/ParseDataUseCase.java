package com.thangnguyen.geocomplyassigment.domain.usecase;

import com.thangnguyen.geocomplyassigment.domain.UseCase;
import com.thangnguyen.geocomplyassigment.domain.entity.WebContent;
import com.thangnguyen.geocomplyassigment.domain.repository.LinkRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;


public class ParseDataUseCase extends UseCase<String, JSONObject> {

    private static final String PRE_MENTION = "@";
    private static final String PRE_HTTP = "http://";
    private static final String PRE_HTTPS = "https://";
    private static final String REGEX_NON_WORD = "\\W";
    private static final String REGEX_SPECIAL_CHARACTER = "^[^A-Za-z0-9-@]|[^A-Za-z0-9]$";

    private static final String KEY_MENTIONS = "mentions";
    private static final String KEY_LINKS = "links";
    private static final String KEY_URL = "url";
    private static final String KEY_TITLE = "title";

    private final LinkRepository linkRepository;

    @Inject
    public ParseDataUseCase(ExecutorService executor, LinkRepository linkRepository) {
        super(executor);
        this.linkRepository = linkRepository;
    }

    @Override
    protected void onExecute() {
        if (getRequest() == null || getRequest().trim().isEmpty()) {
            fail(new IllegalArgumentException("Empty"));
            return;
        }

        List<String> users = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        // remove special characters at start and end
        String result = getRequest().replaceAll(REGEX_SPECIAL_CHARACTER, "");
        Scanner scan = new Scanner(result);
        while (scan.hasNext()) {
            String wordFound = scan.next();
            if (wordFound.length() > 1) {
                if (wordFound.startsWith(PRE_MENTION)) {
                    String userName = wordFound.substring(1);
                    int lastPosition = findFirstNonWordPosition(userName);
                    users.add(userName.substring(0, lastPosition));
                } else if (wordFound.startsWith(PRE_HTTP) || wordFound.startsWith(PRE_HTTPS)) {
                    urls.add(wordFound);
                }
            }
        }
        List<WebContent> webContents = getTitleUrl(urls);
        pass(generateJsonData(users, webContents));
    }

    private JSONObject generateJsonData(List<String> users, List<WebContent> webContents) {
        JSONObject result = new JSONObject();

        // mentions
        if (users != null && !users.isEmpty()) {
            JSONArray jsonMentions = new JSONArray();
            for (String user : users) {
                jsonMentions.put(user);
            }
            try {
                result.put(KEY_MENTIONS, jsonMentions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // links
        if (webContents != null && !webContents.isEmpty()) {
            JSONArray jsonLinks = new JSONArray();
            for (WebContent webContent : webContents) {
                JSONObject jsWeb = new JSONObject();

                try {
                    jsWeb.put(KEY_URL, webContent.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsWeb.put(KEY_TITLE, webContent.getTitle());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonLinks.put(jsWeb);
            }

            try {
                result.put(KEY_LINKS, jsonLinks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Vector<WebContent> getTitleUrl(List<String> urls) {
        Vector<WebContent> contents = new Vector<>();
        List<Future<?>> requests = new ArrayList<>();
        for (String url : urls) {
            requests.add(getExecutor().submit(() -> {
                String title = linkRepository.getTitle(url);
                contents.add(new WebContent(url, title));
            }));
        }

        // wait for all requests
        for (Future<?> future : requests) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return contents;
    }

    private int findFirstNonWordPosition(String string) {
        if (string == null) {
            return 0;
        }
        for (int i = 0; i < string.length(); i++) {
            if (String.valueOf(string.charAt(i)).matches(REGEX_NON_WORD)) {
                return i;
            }
        }
        return string.length();
    }
}

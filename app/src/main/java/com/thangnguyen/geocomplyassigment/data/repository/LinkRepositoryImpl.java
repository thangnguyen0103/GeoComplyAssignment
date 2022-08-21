package com.thangnguyen.geocomplyassigment.data.repository;

import com.thangnguyen.geocomplyassigment.domain.repository.LinkRepository;

import org.jsoup.Jsoup;

import java.io.IOException;


public class LinkRepositoryImpl implements LinkRepository {

    @Override
    public String getTitle(String url) {
        try {
            return Jsoup.connect(url).timeout(60000).get().title();
        } catch (IOException e) {
            return "";
        }
    }
}

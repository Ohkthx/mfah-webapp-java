package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.ArtistArtWork;
import com.sun.net.httpserver.HttpHandler;

public class ArtistWorkHandler  implements HttpHandler {
    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

            String response = Utils.dynamicNavigator(exchange, "artwork.html");
            response = response.replace("{{artistwork}}", getArtist());

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
    }


    public String getArtist() {
        String artistwork ="";
        for(ArtistArtWork artistArtWork : Database.getArtistArtWork()) {
            artistwork += "<tr>";
            artistwork += "<td>" + artistArtWork.getFirstName() + "</td>";
            artistwork += "<td>" + artistArtWork.getLastName() + "</td>";
            artistwork += "<td>" + artistArtWork.getArtworkTitle() + "</td>";
            artistwork += "</tr>";
        }


        return  artistwork;
    }

}

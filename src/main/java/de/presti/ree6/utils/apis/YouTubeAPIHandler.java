package de.presti.ree6.utils.apis;

import de.presti.wrapper.YouTubeWrapper;
import de.presti.wrapper.entities.VideoResult;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.entities.channel.ChannelShortResult;
import de.presti.wrapper.entities.channel.ChannelVideoResult;
import de.presti.wrapper.entities.search.ChannelSearchResult;
import de.presti.wrapper.entities.search.SearchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// TODO:: check if there is a way to make this more efficient, maybe use a cache system or merge multiple requests into one and split the result for further use again?

/**
 * YouTubeAPIHandler.
 */
@Slf4j
public class YouTubeAPIHandler {

    /**
     * The YouTube API-Handler.
     */
    public static YouTubeAPIHandler instance;

    /**
     * Constructor.
     */
    public YouTubeAPIHandler() {
        instance = this;
    }

    /**
     * Search on YouTube a specific query.
     *
     * @param search The query.
     * @return A link to the first Video result.
     * @throws Exception if there was a search problem.
     */
    public String searchYoutube(String search) throws Exception {
        List<SearchResult> results = YouTubeWrapper.search(search, SearchResult.FILTER.VIDEO);

        if (!results.isEmpty()) {
            String videoId = results.get(0).getId();

            return "https://www.youtube.com/watch?v=" + videoId;
        }

        return null;
    }

    /**
     * Get the YouTube uploads of a specific user.
     *
     * @param channelId The channel id.
     * @return A list of all Video ids.
     * @throws Exception if something went wrong.
     */
    public List<VideoResult> getYouTubeUploads(String channelId) throws Exception {
        List<VideoResult> playlistItemList = new ArrayList<>();

        if (isValidChannelId(channelId)) {
            ChannelVideoResult channelVideo = YouTubeWrapper.getChannelVideo(channelId);

            // Convert it to an actual Video instead of a stripped down version.
            for (VideoResult video : channelVideo.getVideos()) {
                playlistItemList.add(YouTubeWrapper.getVideo(video.getId(), false));
            }

            ChannelShortResult channelShorts = YouTubeWrapper.getChannelShort(channelId);

            for (VideoResult shorts : channelShorts.getShorts()) {
                playlistItemList.add(YouTubeWrapper.getVideo(shorts.getId(), true));
            }
        }

        return playlistItemList;
    }

    /**
     * Get an YouTube channel by id.
     *
     * @param channelName The channel name.
     * @return The channel.
     * @throws Exception if something went wrong.
     */
    public ChannelResult getYouTubeChannelBySearch(String channelName) throws Exception {
        ChannelSearchResult channelSearchResult = (ChannelSearchResult) YouTubeWrapper.search(channelName, SearchResult.FILTER.CHANNEL).get(0);

        if (channelSearchResult == null) {
            return null;
        }

        return YouTubeWrapper.getChannel(channelSearchResult.getId());
    }

    /**
     * Get an YouTube channel by id.
     *
     * @param channelId The channel id.
     * @return The channel.
     * @throws Exception if something went wrong.
     */
    public ChannelResult getYouTubeChannelById(String channelId) throws Exception {
        return YouTubeWrapper.getChannel(channelId);
    }

    /**
     * Check if a given channel ID matches the pattern of a YouTube channel ID.
     *
     * @param channelId The channel ID.
     * @return True if it matches, false if not.
     */
    public boolean isValidChannelId(String channelId) {
        return channelId.matches("^UC[\\w-]{21}[AQgw]$");
    }


    /**
     * Method used to return an instance of the handler.
     *
     * @return instance of the handler.
     */
    public static YouTubeAPIHandler getInstance() {
        if (instance == null) {
            return instance = new YouTubeAPIHandler();
        }
        return instance;
    }
}

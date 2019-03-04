package tcss450.uw.edu.phishappwolfr1.Content;

import android.text.Html;

import java.io.Serializable;

public class SetList implements Serializable {


    private final String mShowDate;
    private final String mLocation;
    private final String mSetListData;
    private final String mSetListNotes;
    private final String mVenue;
    private final String mUrl;


    /**
     * Helper class for building Set Lists.
     *
     * @author Robert Wolf
     */
    public static class Builder {
        private final String mShowDate;
        private final String mLocation;
        private final String mVenue;
        private String mSetListData ="";
        private String mSetListNotes="";
        private  String mUrl = "";


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the show date of the set list
         * @param location the location of the show
         * @param venue the venue of the set list
         */
        public Builder(String pubDate, String location, String venue) {
            this.mShowDate = pubDate;
            this.mLocation = location;
            this.mVenue = venue;
        }

        /**
         * Add an optional url for the full setlist post.
         * @param val an optional url for the full setlist post
         * @return the Builder of this SetList
         */
        public SetList.Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add data for the setlist post.
         * @param data an optional url teaser for the full setlist post.
         * @return the Builder of this SetList
         */
        public SetList.Builder addSetListData(final String data) {
            mSetListData = Html.fromHtml(data).toString();
            return this;
        }

        /**
         * Add set list notes.
         * @param val an optional notes for the set list.
         * @return the Builder of this SetList
         */
        public SetList.Builder addNotes(final String val) {
            mSetListNotes = val;
            return this;
        }

        public SetList build() {
            return new SetList(this);
        }

    }

    private SetList(final SetList.Builder builder) {
        this.mUrl = builder.mUrl;
        this.mShowDate = builder.mShowDate;
        this.mLocation = builder.mLocation;
        this.mSetListData = builder.mSetListData;
        this.mSetListNotes = builder.mSetListNotes;
        this.mVenue = builder.mVenue;
    }

    public String getShowDate() {
        return mShowDate;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSetListData() {
        return mSetListData;
    }

    public String getmSetListNotes() {
        return mSetListNotes;
    }


    public String getVenue() {
        return mVenue;
    }




}

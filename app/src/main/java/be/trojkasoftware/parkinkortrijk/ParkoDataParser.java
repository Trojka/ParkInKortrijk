package be.trojkasoftware.parkinkortrijk;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergedesmedt on 2/12/14.
 */
public class ParkoDataParser {
    private static final String ns = null;

    // We don't use namespaces

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            //parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        int event;
        String text = "";
        try {
            event = parser.getEventType();
            Entry entry = null;
            boolean isGeneralInfo = false;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("OffstreetParking")) {
                            entry = new Entry();
                        }
                        if(name.equals("GeneralInfo")) {
                            isGeneralInfo = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("Name") && entry != null && isGeneralInfo){
                            entry.name = text;
                        }
                        if(name.equals("Latitude") && entry != null && isGeneralInfo){
                            entry.latitude = text;
                        }
                        if(name.equals("Longitude") && entry != null && isGeneralInfo){
                            entry.longitude = text;
                        }
                        if(name.equals("GeneralInfo")) {
                            isGeneralInfo = false;
                        }
                        if(name.equals("OffstreetParking")) {
                            entries.add(entry);
                            entry = null;
                        }
                        break;
                }
                event = parser.next();

            }
            //parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }



//        parser.require(XmlPullParser.START_TAG, ns, "ITSPS");
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String name = parser.getName();
//            // Starts by looking for the entry tag
//            if (name.equals("OffstreetParking")) {
//                entries.add(readEntry(parser));
////            } else {
////                skip(parser);
//            }
//        }
        return entries;
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Entry {
        public String name;
        public String latitude;
        public String longitude;
    }

//    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
//    // off
//    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
//    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "OffstreetParking");
//        String title = null;
//        String summary = null;
//        String link = null;
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String name = parser.getName();
//            if (name.equals("Name")) {
//                title = readTitle(parser);
//            } else if (name.equals("summary")) {
//                summary = readSummary(parser);
//            } else if (name.equals("link")) {
//                link = readLink(parser);
////            } else {
////                skip(parser);
//            }
//        }
//        return new Entry(title, summary, link);
//    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Name");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

//    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
//    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
//    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
//    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
//        if (parser.getEventType() != XmlPullParser.START_TAG) {
//            throw new IllegalStateException();
//        }
//        int depth = 1;
//        while (depth != 0) {
//            switch (parser.next()) {
//                case XmlPullParser.END_TAG:
//                    depth--;
//                    break;
//                case XmlPullParser.START_TAG:
//                    depth++;
//                    break;
//            }
//        }
//    }
}

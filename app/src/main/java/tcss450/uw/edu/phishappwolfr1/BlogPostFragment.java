package tcss450.uw.edu.phishappwolfr1;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450.uw.edu.phishappwolfr1.Content.BlogPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogPostFragment extends Fragment  {

    private BlogPostFragment.OnBlogPostFragmentInterractionListener mListener;

    BlogPost myPost;

    public BlogPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_post, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            myPost = (BlogPost) getArguments().get("");

            TextView tv = getActivity().findViewById(R.id.BlogPostTitleView);
            tv.setText(myPost.getAuthor());

            tv = getActivity().findViewById(R.id.BlogPostPubDateView);
            tv.setText(myPost.getPubDate());

            tv = getActivity().findViewById(R.id.BlogPostContentView);
            tv.setText(Html.fromHtml(myPost.getTeaser()).toString());
        }

        Button b = (Button) getActivity().findViewById(R.id.BlogPostViewFull);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFullView(myPost);
            }
        });
    }

    private void onClickFullView(BlogPost b) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(b.getUrl()));
        startActivity(myIntent);
    }



    public interface OnBlogPostFragmentInterractionListener {
        void onViewFullBlogClick(BlogPost B);
    }

}

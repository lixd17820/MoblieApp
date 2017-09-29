package com.jwt.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jwt.adapter.MainMenuAdapter;
import com.jwt.bean.MenuGridBean;
import com.jwt.bean.MenuOptionBean;
import com.jwt.pojo.Bjbd;
import com.jwt.pojo.Bjbd_;
import com.jwt.update.R;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.MenuParser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuJbywFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MenuJbywFragment extends Fragment {

    private MainMenuAdapter menusAdapter;
    private List<MenuGridBean> menuLists;
    private List<MenuOptionBean> menuOptionList;

    private OnFragmentInteractionListener mListener;

    public MenuJbywFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("jbywFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_jbyw, container, false);
    }

    @Override
    public void onStart() {
        Log.e("jbywFragment", "onStart");
        super.onStart();
        setUpViews();
    }

    protected void setUpViews() {
        Activity self = getActivity();
        View v = getView();
        long bjwd = GlobalMethod.getBoxStore(self).boxFor(Bjbd.class).query()
                .notEqual(Bjbd_.ydbj, 1).build().count();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.gridView1);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(self, 3));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        menuLists = MenuParser.parseMenuXml(self);
        // 根据权限过滤菜单
        //menuLists = MenuParser.filterMenuByQx(menuLists, GlobalData.grxx.get("YHLX"));
        menuOptionList = menuLists.get(0).getOptions();
        menusAdapter = new MainMenuAdapter(menuClickListener, 3);
        recyclerView.setAdapter(menusAdapter);
        for (MenuOptionBean opt : menuOptionList) {
            if ("com.jwt.update.JbywBjbdListActivity".equals(opt.getClassName())) {
                opt.setBadge(bjwd > 0);
            }
        }
        menusAdapter.setMenus(menuOptionList);

    }

    private MainMenuAdapter.MenuClickListener menuClickListener = new MainMenuAdapter.MenuClickListener() {
        @Override
        public void onNoteClick(int position) {
            MenuOptionBean m = menuOptionList.get(position);
            //m.setBadge(false);
            //menusAdapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(m.getPck())
                    && !TextUtils.isEmpty(m.getClassName())) {
                String dn = m.getDataName();
                String data = m.getData();
                if (TextUtils.equals(dn, "out")) {
                    Intent intent = new Intent(m.getClassName()); //广播内容
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    getActivity().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    Log.e("MainActivity", m.getPck() + "/" + m.getClassName());
                    intent.setComponent(new ComponentName(m.getPck(), m
                            .getClassName()));
                    if (!TextUtils.isEmpty(dn)
                            && !TextUtils.isEmpty(data)) {
                        intent.putExtra(m.getDataName(), m.getData());
                    }
                    intent.putExtra("title", m.getMenuName());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

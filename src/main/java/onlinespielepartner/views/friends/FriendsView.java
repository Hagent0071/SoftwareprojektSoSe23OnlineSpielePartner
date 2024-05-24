package onlinespielepartner.views.friends;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import onlinespielepartner.views.MainLayout;

@PageTitle("Friends")
@Route(value = "friends", layout = MainLayout.class)
@RolesAllowed("USER")
public class FriendsView extends Composite<VerticalLayout> {

    public FriendsView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}

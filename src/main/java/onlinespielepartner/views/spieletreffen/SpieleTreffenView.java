package onlinespielepartner.views.spieletreffen;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import onlinespielepartner.views.MainLayout;

@PageTitle("SpieleTreffen")
@Route(value = "spieleTreffen", layout = MainLayout.class)
@RolesAllowed("USER")
public class SpieleTreffenView extends Composite<VerticalLayout> {

    public SpieleTreffenView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}

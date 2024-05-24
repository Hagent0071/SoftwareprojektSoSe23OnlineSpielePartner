package onlinespielepartner.views.verwaltung;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.time.Duration;
import java.util.Optional;
import onlinespielepartner.data.OnlineSpieleTreffen;
import onlinespielepartner.services.OnlineSpieleTreffenService;
import onlinespielepartner.views.MainLayout;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Verwaltung")
@Route(value = "verwaltung/:onlineSpieleTreffenID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class VerwaltungView extends Div implements BeforeEnterObserver {

    private final String ONLINESPIELETREFFEN_ID = "onlineSpieleTreffenID";
    private final String ONLINESPIELETREFFEN_EDIT_ROUTE_TEMPLATE = "verwaltung/%s/edit";

    private final Grid<OnlineSpieleTreffen> grid = new Grid<>(OnlineSpieleTreffen.class, false);

    private TextField titel;
    private TextField beschreibung;
    private DateTimePicker datum;
    private TextField link;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<OnlineSpieleTreffen> binder;

    private OnlineSpieleTreffen onlineSpieleTreffen;

    private final OnlineSpieleTreffenService onlineSpieleTreffenService;

    public VerwaltungView(OnlineSpieleTreffenService onlineSpieleTreffenService) {
        this.onlineSpieleTreffenService = onlineSpieleTreffenService;
        addClassNames("verwaltung-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("titel").setAutoWidth(true);
        grid.addColumn("beschreibung").setAutoWidth(true);
        grid.addColumn("datum").setAutoWidth(true);
        grid.addColumn("link").setAutoWidth(true);
        grid.setItems(query -> onlineSpieleTreffenService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent()
                        .navigate(String.format(ONLINESPIELETREFFEN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(VerwaltungView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(OnlineSpieleTreffen.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.onlineSpieleTreffen == null) {
                    this.onlineSpieleTreffen = new OnlineSpieleTreffen();
                }
                binder.writeBean(this.onlineSpieleTreffen);
                onlineSpieleTreffenService.update(this.onlineSpieleTreffen);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(VerwaltungView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> onlineSpieleTreffenId = event.getRouteParameters().get(ONLINESPIELETREFFEN_ID)
                .map(Long::parseLong);
        if (onlineSpieleTreffenId.isPresent()) {
            Optional<OnlineSpieleTreffen> onlineSpieleTreffenFromBackend = onlineSpieleTreffenService
                    .get(onlineSpieleTreffenId.get());
            if (onlineSpieleTreffenFromBackend.isPresent()) {
                populateForm(onlineSpieleTreffenFromBackend.get());
            } else {
                Notification.show(String.format("The requested onlineSpieleTreffen was not found, ID = %s",
                        onlineSpieleTreffenId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(VerwaltungView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        titel = new TextField("Titel");
        beschreibung = new TextField("Beschreibung");
        datum = new DateTimePicker("Datum");
        datum.setStep(Duration.ofSeconds(1));
        link = new TextField("Link");
        formLayout.add(titel, beschreibung, datum, link);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(OnlineSpieleTreffen value) {
        this.onlineSpieleTreffen = value;
        binder.readBean(this.onlineSpieleTreffen);

    }
}

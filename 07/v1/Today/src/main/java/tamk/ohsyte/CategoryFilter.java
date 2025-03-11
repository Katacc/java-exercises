package tamk.ohsyte;

public class CategoryFilter extends EventFilter {

    Category cat;


    public CategoryFilter(Category category) {

        this.cat = category;

    }




    @Override
    public boolean accepts(Event event) {

        if (this.cat.toString().equals(event.getCategory().toString())) {
            return true;
        } else {
            return false;
        }

    }
}

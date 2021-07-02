package it.polimi.ingsw.client.cli.components;

import java.util.List;

public class UnorderedList extends StringComponent {
    List<String> list;

    public UnorderedList(List<String> list) {
        this.list = List.copyOf(list);
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(item -> stringBuilder.append(String.format("  • %s", item)).append("\n"));
        return stringBuilder.toString();
    }
}

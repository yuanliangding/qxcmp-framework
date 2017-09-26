package com.qxcmp.framework.web.view.support.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.qxcmp.framework.web.view.annotation.table.EntityTable;
import com.qxcmp.framework.web.view.annotation.table.RowActionCheck;
import com.qxcmp.framework.web.view.annotation.table.TableField;
import com.qxcmp.framework.web.view.elements.button.AbstractButton;
import com.qxcmp.framework.web.view.elements.button.Button;
import com.qxcmp.framework.web.view.elements.button.Buttons;
import com.qxcmp.framework.web.view.elements.header.HeaderType;
import com.qxcmp.framework.web.view.elements.header.PageHeader;
import com.qxcmp.framework.web.view.elements.image.Avatar;
import com.qxcmp.framework.web.view.elements.label.Label;
import com.qxcmp.framework.web.view.elements.label.Labels;
import com.qxcmp.framework.web.view.modules.form.FormMethod;
import com.qxcmp.framework.web.view.modules.pagination.Pagination;
import com.qxcmp.framework.web.view.modules.table.*;
import com.qxcmp.framework.web.view.modules.table.dictionary.AbstractDictionaryValueCell;
import com.qxcmp.framework.web.view.support.Alignment;
import com.qxcmp.framework.web.view.support.Size;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
@RequiredArgsConstructor
public class TableHelper {

    public Table convert(Map<String, Object> dictionary) {
        final Table table = new Table();
        table.setCelled().setStriped().setBasic().setSize(Size.SMALL);
        table.setBody(new TableBody());

        if (dictionary.isEmpty()) {
            TableRow tableRow = new TableRow();
            TableData tableData = new TableData();
            tableData.setColSpan(2);
            tableData.setContent("暂无内容");
            tableRow.addCell(tableData);
            table.getBody().addRow(tableRow);
        } else {
            dictionary.forEach((key, value) -> {
                TableRow tableRow = new TableRow();
                TableData keyCell = new TableData();
                keyCell.setContent(key);
                tableRow.addCell(keyCell);

                parseValueCell(tableRow, value);

                table.getBody().addRow(tableRow);
            });
        }

        return table;
    }

    private void parseValueCell(TableRow tableRow, Object value) {

        if (Objects.isNull(value)) {
            tableRow.addCell(new TableData(""));
        } else {
            if (value instanceof Boolean) {
                tableRow.addCell(new TableData(Boolean.parseBoolean(value.toString()) ? "是" : "否"));
            } else if (value instanceof AbstractDictionaryValueCell) {
                AbstractDictionaryValueCell dictionaryValueCell = (AbstractDictionaryValueCell) value;
                tableRow.addCell(dictionaryValueCell.parse());
            } else {
                tableRow.addCell(new TableData(value.toString()));
            }
        }
    }

    public <T> com.qxcmp.framework.web.view.modules.table.EntityTable convert(String tableName, Class<T> tClass, Page<T> tPage) {
        checkNotNull(tableName);

        final com.qxcmp.framework.web.view.modules.table.EntityTable table = new com.qxcmp.framework.web.view.modules.table.EntityTable();

        EntityTable entityTable = Arrays.stream(tClass.getDeclaredAnnotationsByType(EntityTable.class)).filter(annotation -> StringUtils.equals(annotation.name(), tableName)).findAny().orElseThrow(() -> new IllegalStateException("No EntityTable definition"));

        configEntityTable(table, entityTable, tClass);

        renderTableContent(table, entityTable, tClass, tPage);

        return table;
    }

    private <T> void configEntityTable(com.qxcmp.framework.web.view.modules.table.EntityTable table, EntityTable entityTable, Class<T> tClass) {

        if (StringUtils.isNotBlank(entityTable.value())) {
            table.setTableHeader(new PageHeader(HeaderType.H2, entityTable.value()).setDividing());
        }

        table.setEntityIndex(entityTable.entityIndex());

        if (StringUtils.isNotBlank(entityTable.action())) {
            table.setAction(entityTable.action());
        } else {
            table.setAction(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, tClass.getSimpleName()));
        }

        if (!StringUtils.endsWith(table.getAction(), "/")) {
            table.setAction(table.getAction() + "/");
        }

        table.setCelled(entityTable.celled());
        table.setBasic(entityTable.basic());
        table.setVeryBasic(entityTable.veryBasic());
        table.setSingleLine(entityTable.singleLine());
        table.setFixed(entityTable.fixed());
        table.setSelectable(entityTable.selectable());
        table.setStriped(entityTable.striped());
        table.setInverted(entityTable.inverted());
        table.setCollapsing(entityTable.collapsing());
        table.setPadded(entityTable.padded());
        table.setVeryPadded(entityTable.veryPadded());
        table.setCompact(entityTable.compact());
        table.setVeryCompact(entityTable.veryCompact());
        table.setColumnCount(entityTable.columnCount());
        table.setStackDevice(entityTable.stackDevice());
        table.setColor(entityTable.color());
        table.setSize(entityTable.size());

        Arrays.stream(entityTable.tableActions()).forEach(tableAction -> {
            EntityTableAction entityTableAction = new EntityTableAction();

            entityTableAction.setTitle(tableAction.value());
            entityTableAction.setAction(table.getAction() + tableAction.action());
            entityTableAction.setMethod(tableAction.method());
            entityTableAction.setTarget(tableAction.target());
            entityTableAction.setColor(tableAction.color());
            entityTableAction.setPrimary(tableAction.primary());
            entityTableAction.setSecondary(tableAction.secondary());
            entityTableAction.setInverted(tableAction.inverted());
            entityTableAction.setBasic(tableAction.basic());

            table.getTableActions().add(entityTableAction);
        });

        Arrays.stream(entityTable.batchActions()).forEach(batchAction -> {
            EntityTableBatchAction entityTableBatchAction = new EntityTableBatchAction();

            entityTableBatchAction.setTitle(batchAction.value());
            entityTableBatchAction.setAction(table.getAction() + batchAction.action());
            entityTableBatchAction.setMethod(FormMethod.POST);
            entityTableBatchAction.setTarget(batchAction.target());
            entityTableBatchAction.setColor(batchAction.color());
            entityTableBatchAction.setPrimary(batchAction.primary());
            entityTableBatchAction.setSecondary(batchAction.secondary());
            entityTableBatchAction.setInverted(batchAction.inverted());
            entityTableBatchAction.setBasic(batchAction.basic());

            table.getBatchActions().add(entityTableBatchAction);
        });

        Arrays.stream(entityTable.rowActions()).forEach(rowAction -> {
            EntityTableRowAction entityTableRowAction = new EntityTableRowAction();

            entityTableRowAction.setTitle(rowAction.value());
            entityTableRowAction.setAction(rowAction.action());
            entityTableRowAction.setMethod(rowAction.method());
            entityTableRowAction.setTarget(rowAction.target());
            entityTableRowAction.setColor(rowAction.color());
            entityTableRowAction.setPrimary(rowAction.primary());
            entityTableRowAction.setSecondary(rowAction.secondary());
            entityTableRowAction.setInverted(rowAction.inverted());
            entityTableRowAction.setBasic(rowAction.basic());

            table.getRowActions().add(entityTableRowAction);
        });

        table.setMultiple(!table.getBatchActions().isEmpty());
    }

    private <T> void renderTableContent(com.qxcmp.framework.web.view.modules.table.EntityTable table, EntityTable entityTable, Class<T> tClass, Page<T> tPage) {
        final List<EntityTableField> entityTableFields = Lists.newArrayList();

        for (Field field : tClass.getDeclaredFields()) {
            Arrays.stream(field.getDeclaredAnnotationsByType(TableField.class)).filter(tableField -> StringUtils.isBlank(tableField.name()) || StringUtils.equals(tableField.name(), entityTable.name())).findAny().ifPresent(tableField -> {
                EntityTableField entityTableField = new EntityTableField();

                entityTableField.setField(field);
                entityTableField.setTitle(tableField.value());
                entityTableField.setDescription(tableField.description());
                entityTableField.setOrder(tableField.order());
                entityTableField.setFieldSuffix(tableField.fieldSuffix());
                entityTableField.setMaxCollectionCount(tableField.maxCollectionCount());
                entityTableField.setCollectionEntityIndex(tableField.collectionEntityIndex());
                entityTableField.setImage(tableField.image());
                entityTableField.setEnableUrl(tableField.enableUrl());

                if (StringUtils.isNotBlank(tableField.urlPrefix())) {
                    entityTableField.setUrlPrefix(tableField.urlPrefix());

                    if (!StringUtils.endsWith(entityTableField.getUrlPrefix(), "/")) {
                        entityTableField.setUrlPrefix(entityTableField.getUrlPrefix() + "/");
                    }
                } else {
                    entityTableField.setUrlPrefix(table.getAction());
                }

                if (StringUtils.isNotBlank(tableField.urlEntityIndex())) {
                    entityTableField.setUrlEntityIndex(tableField.urlEntityIndex());
                } else {
                    entityTableField.setUrlEntityIndex(entityTableField.getCollectionEntityIndex());
                }

                entityTableField.setUrlSuffix(tableField.urlSuffix());

                entityTableFields.add(entityTableField);
            });
        }

        entityTableFields.sort(Comparator.comparingInt(EntityTableField::getOrder));

        renderTableHeader(table, entityTableFields);

        renderTableBody(table, entityTableFields, tClass, tPage);

        renderTableFooter(table, entityTableFields, tPage);
    }

    private void renderTableHeader(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields) {
        final TableHeader tableHeader = new TableHeader();

        if (!table.getTableActions().isEmpty()) {
            renderTableActionHeader(table, entityTableFields, tableHeader);
        }

        if (!table.getBatchActions().isEmpty()) {
            renderTableBatchActionHeader(table, entityTableFields, tableHeader);
        }

        renderTableTitleHeader(table, entityTableFields, tableHeader);

        table.setHeader(tableHeader);
    }

    private void renderTableActionHeader(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields, TableHeader tableHeader) {
        final TableRow tableRow = new TableRow();
        final TableHead tableHead = new TableHead();
        final Buttons buttons = new Buttons();

        int colSpan = entityTableFields.size();

        if (table.isMultiple()) {
            ++colSpan;
        }

        if (!table.getRowActions().isEmpty()) {
            ++colSpan;
        }

        tableHead.setColSpan(colSpan);
        buttons.setSize(Size.MINI);

        table.getTableActions().forEach(entityTableAction -> {
            buttons.addButton(convertActionToButton(entityTableAction));
        });

        tableHead.setComponent(buttons);
        tableRow.addCell(tableHead);
        tableHeader.addRow(tableRow);
    }

    private void renderTableBatchActionHeader(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields, TableHeader tableHeader) {
        final TableRow tableRow = new TableRow();
        final TableHead tableHead = new TableHead();
        final Buttons buttons = new Buttons();

        int colSpan = entityTableFields.size();

        if (table.isMultiple()) {
            ++colSpan;
        }

        if (!table.getRowActions().isEmpty()) {
            ++colSpan;
        }

        tableHead.setColSpan(colSpan);
        buttons.setSize(Size.MINI);

        table.getBatchActions().forEach(entityTableBatchAction -> {
            buttons.addButton(convertActionToButton(entityTableBatchAction));
        });

        tableHead.setComponent(buttons);
        tableRow.addCell(tableHead);
        tableHeader.addRow(tableRow);
    }

    private void renderTableTitleHeader(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields, TableHeader tableHeader) {
        final TableRow tableRow = new TableRow();

        if (table.isMultiple()) {
            tableRow.addCell(new TableHeadCheckbox("root"));
        }

        entityTableFields.forEach(entityTableField -> {
            final TableHead tableHead = new TableHead();
            tableHead.setContent(entityTableField.getTitle());
            tableRow.addCell(tableHead);
        });

        if (!table.getRowActions().isEmpty()) {
            final TableHead tableHead = new TableHead();
            tableHead.setContent("操作");
            tableRow.addCell(tableHead);
        }

        tableHeader.addRow(tableRow);
    }

    private <T> void renderTableBody(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields, Class<T> tClass, Page<T> tPage) {
        final TableBody tableBody = new TableBody();

        tPage.getContent().forEach(t -> {
            final TableRow tableRow = new TableRow();

            if (table.isMultiple()) {
                final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(t);
                tableRow.addCell(new TableDataCheckbox(beanWrapper.getPropertyValue(table.getEntityIndex()).toString()));
            }

            entityTableFields.forEach(entityTableField -> {
                final TableData tableData = new TableData();

                renderTableCell(tableData, entityTableField, t);

                tableRow.addCell(tableData);
            });

            if (!table.getRowActions().isEmpty()) {
                final TableData tableData = new TableData();

                renderTableActionCell(table, tableData, table.getRowActions(), tClass, t);
                tableRow.addCell(tableData);
            }

            tableBody.addRow(tableRow);
        });

        table.setBody(tableBody);
    }

    @SuppressWarnings("unchecked")
    private <T> void renderTableCell(TableData tableData, EntityTableField entityTableField, T t) {
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(t);

        Object value = beanWrapper.getPropertyValue(entityTableField.getField().getName() + entityTableField.getFieldSuffix());

        if (entityTableField.isImage()) {
            tableData.setCollapsing().setComponent(new Avatar(Objects.nonNull(value) ? value.toString() : ""));
        } else if (Collection.class.isAssignableFrom(entityTableField.getField().getType())) {
            String entityIndex = entityTableField.getCollectionEntityIndex();
            List list = (List) ((Collection) value).stream().limit(entityTableField.getMaxCollectionCount()).collect(Collectors.toList());
            final Labels labels = new Labels();
            list.forEach(item -> {

                BeanWrapperImpl itemWrapper = new BeanWrapperImpl(item);

                String labelText;

                Object itemValue = itemWrapper.getPropertyValue(entityIndex);

                if (StringUtils.isNotBlank(entityIndex)) {
                    labelText = Objects.nonNull(itemValue) ? itemValue.toString() : "";
                } else {
                    labelText = item.toString();
                }

                if (entityTableField.isEnableUrl()) {
                    String url = entityTableField.getUrlPrefix() + itemWrapper.getPropertyValue(entityTableField.getUrlEntityIndex()) + "/" + entityTableField.getUrlSuffix();
                    labels.addLabel(new Label(labelText).setUrl(url));
                } else {
                    labels.addLabel(new Label(labelText));
                }
            });
            tableData.setComponent(labels);
        } else {
            tableData.setContent(Objects.nonNull(value) ? value.toString() : "");
        }
    }

    private <T> void renderTableActionCell(com.qxcmp.framework.web.view.modules.table.EntityTable table, TableData tableData, List<EntityTableRowAction> rowActions, Class<T> tClass, T t) {
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(t);
        final Buttons buttons = new Buttons();

        buttons.setSize(Size.MINI);

        rowActions.forEach(entityTableRowAction -> {

            Method checkMethod = Arrays.stream(tClass.getDeclaredMethods()).filter(method -> {
                if (method.getReturnType().equals(boolean.class)) {
                    for (RowActionCheck rowActionCheck : method.getAnnotationsByType(RowActionCheck.class)) {
                        if (StringUtils.equals(rowActionCheck.value(), entityTableRowAction.getTitle())) {
                            return true;
                        }
                    }
                }
                return false;
            }).findFirst().orElse(null);

            boolean canPerform = true;

            if (Objects.nonNull(checkMethod)) {
                try {
                    canPerform = (boolean) checkMethod.invoke(t);
                } catch (Exception ignored) {

                }
            }

            if (canPerform) {
                AbstractButton button = convertActionToButton(entityTableRowAction);
                button.getAnchor().setHref(table.getAction() + beanWrapper.getPropertyValue(table.getEntityIndex()) + "/" + entityTableRowAction.getAction());
                button.getAnchor().setTarget(entityTableRowAction.getTarget().toString());
                buttons.addButton(button);
            }
        });

        tableData.setComponent(buttons);
    }

    private <T> void renderTableFooter(com.qxcmp.framework.web.view.modules.table.EntityTable table, List<EntityTableField> entityTableFields, Page<T> tPage) {
        final TableFooter tableFooter = new TableFooter();
        final TableRow tableRow = new TableRow();
        final TableHead tableHead = new TableHead();

        int colSpan = entityTableFields.size();

        if (table.isMultiple()) {
            ++colSpan;
        }

        if (!table.getRowActions().isEmpty()) {
            ++colSpan;
        }

        tableHead.setColSpan(colSpan);
        tableHead.setAlignment(Alignment.CENTER);
        tableHead.setComponent(new Pagination("", tPage.getNumber() + 1, (int) tPage.getTotalElements(), tPage.getSize()).setShowSizeChanger().setShowQuickJumper().setShowTotal());

        tableRow.addCell(tableHead);
        tableFooter.addRow(tableRow);
        table.setFooter(tableFooter);
    }

    private AbstractButton convertActionToButton(AbstractEntityTableAction tableAction) {
        AbstractButton button;

        if (tableAction.getMethod().equals(FormMethod.NONE)) {
            button = new Button(tableAction.getTitle(), tableAction.getAction(), tableAction.getTarget());
        } else {
            if (tableAction instanceof EntityTableBatchAction) {
                button = new EntityTableBatchActionButton(tableAction.getTitle(), tableAction.getAction(), tableAction.getTarget());
            } else {
                button = new EntityTableActionButton(tableAction.getTitle(), tableAction.getAction(), tableAction.getTarget());
            }
        }

        button.setColor(tableAction.getColor());
        button.setPrimary(tableAction.isPrimary());
        button.setSecondary(tableAction.isSecondary());
        button.setInverted(tableAction.isInverted());
        button.setBasic(tableAction.isBasic());

        return button;
    }
}

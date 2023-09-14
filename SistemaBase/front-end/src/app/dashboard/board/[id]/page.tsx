"use client";

//import { DragDropContext, Droppable, Draggable, DropResult } from "react-beautiful-dnd";
import { DndContext, useDroppable, useDraggable, DragEndEvent, DragStartEvent } from '@dnd-kit/core';
import { useEffect, useMemo, useState } from 'react';
import { CSS } from '@dnd-kit/utilities';
import { SortableContext, useSortable } from '@dnd-kit/sortable';

type CheckList = {
    name: string,
    id: string,
    items: CheckListItem[],
}

type CheckListItem = {
    name: string,
    completed: boolean,
    checklistId: string,
}

type Card = {
    title: string,
    id: string,
    columnID: string,
    description: string,
    checklists: CheckList[],
}

type Column = {
    title: string,
    type: number,
    id: string,
    cardsList: Card[],
}

type KanbanData = {
    columns: Column[],
    columnOrder: String[],
    kanbanId: string,
}

function CheckListItemElement(props: CheckListItem) {
    const [checkState, setCheckState] = useState<boolean>(props.completed);
    const handleCheckState = () => setCheckState(!checkState);
    return (
        <div className="flex my-1">
            <input onClick={handleCheckState} className="mr-2" type="checkbox" checked={checkState} id={props.name} />
            <label htmlFor={props.name}>{props.name}</label>
        </div>
    );
}

function CheckListElement(props: CheckList) {
    return (
        <div>
            <h1>{props.name}</h1>
            <div>
                {props.items.map((check: CheckListItem) => {
                    return (
                        <CheckListItemElement name={check.name} completed={check.completed} checklistId={props.id} key={props.id} />
                    );
                })}
            </div>
        </div>
    );
}

interface CardProps extends Card {
    index: number,
}

function CardElement(props: CardProps) {
    const { attributes, listeners, setNodeRef, transform } = useDraggable({
        id: props.id,
    });
    const style = {
        transform: CSS.Translate.toString(transform),
    }

    return (
        <div ref={setNodeRef} {...listeners} {...attributes} style={style} className="w-full bg-neutral-50 rounded-md my-4 p-2">
            <h1>{props.title}</h1>
            <p>{props.description}</p>
            <div>
                {props.checklists.map((item: CheckList) => <CheckListElement id={item.id} name={item.name} items={item.items} />)}
            </div>
        </div>
    );
}

interface ColumnProps extends Column {
    removeFunc: any,
}

function ColumnElement(props: ColumnProps) {
    const handleRemove = () => {
        props.removeFunc(props.id)
    }

    const { setNodeRef, attributes, listeners, transform, transition } = useSortable({
        id: props.id,
        data: {
            dragType: "COLUMN",
            ...props,
        }
    });

    const style = {
        transition,
        transform: CSS.Transform.toString(transform),
    }

    return (
        <div ref={setNodeRef}
            style={style}
            {...attributes}
            {...listeners}
            className="w-64">
            <h1>{props.title}</h1>
            <h2>{props.type}</h2>
            <button onClick={handleRemove}>
                Delete
            </button>
            <div>
                {props.cardsList.map((cardEl: Card, index: number) => <CardElement index={index} title={cardEl.title} id={cardEl.id} columnID={props.id} checklists={cardEl.checklists} description={cardEl.description} />)}
            </div>
        </div>
    );
}

const data: KanbanData = {
    columnOrder: ['column-0'],
    kanbanId: 'aaaaaaaaaa-bbbbbbbbbb-cccccccccc',
    columns: [
        {
            id: 'column-0',
            type: 0,
            title: "Column 00",
            cardsList: [
                {
                    title: "Card 00",
                    id: 'card-0',
                    columnID: 'column-0',
                    description: "Example Card",
                    checklists: [
                        {
                            name: "CheckList 00",
                            id: 'checklist-0',
                            items: [
                                {
                                    name: "Item 00",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },
                                {
                                    name: "Item 01",
                                    completed: true,
                                    checklistId: 'checklist-0'
                                },

                                {
                                    name: "Item 02",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },

                            ]
                        },
                    ],
                },
                {
                    title: "Card 01",
                    id: 'card-1',
                    columnID: 'column-0',
                    description: "Example Card",
                    checklists: [
                        {
                            name: "CheckList 00",
                            id: 'checklist-0',
                            items: [
                                {
                                    name: "Item 00",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },
                                {
                                    name: "Item 01",
                                    completed: true,
                                    checklistId: 'checklist-0'
                                },

                                {
                                    name: "Item 02",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },

                            ]
                        },
                    ],
                },
                {
                    title: "Card 02",
                    id: 'card-2',
                    columnID: 'column-0',
                    description: "Example Card",
                    checklists: [
                        {
                            name: "CheckList 00",
                            id: 'checklist-0',
                            items: [
                                {
                                    name: "Item 00",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },
                                {
                                    name: "Item 01",
                                    completed: true,
                                    checklistId: 'checklist-0'
                                },

                                {
                                    name: "Item 02",
                                    completed: false,
                                    checklistId: 'checklist-0'
                                },

                            ]
                        },
                    ],
                },
            ],
        },
    ],
};

export default function Page({ params }: { params: { id: string } }) {
    const [kanbanData, setKanbanData] = useState<KanbanData>(data);
    const columnsID: any = kanbanData.columnOrder;
    const addColumn = () => {
        setKanbanData((prevKanbanData) => {
            const newColumnID = `column-${prevKanbanData.columnOrder.length}`;
            const newColumn = {
                type: 0,
                id: newColumnID,
                cardsList: [],
                title: `Column ${prevKanbanData.columnOrder.length}`,
            };
            return {
                ...prevKanbanData,
                columnOrder: [...prevKanbanData.columnOrder, newColumnID],
                columns: {
                    ...prevKanbanData.columns,
                    [newColumnID]: newColumn,
                },
            };
        });
    };

    const removeColumn = (columnIDToRemove: string) => {
        setKanbanData((prevKanbanData) => {
            // Create a copy of the columns object without the specified columnID
            const updatedColumns = { ...prevKanbanData.columns };
            delete updatedColumns[columnIDToRemove];

            // Create a copy of the columnOrder array without the removed columnID
            const updatedColumnOrder = prevKanbanData.columnOrder.filter(
                (columnID) => columnID !== columnIDToRemove
            );

            return {
                ...prevKanbanData,
                columns: updatedColumns,
                columnOrder: updatedColumnOrder,
            };
        });
    }

    const onDragEnd = (event: DragEndEvent) => {
        const { active, over } = event;
        const activeColumnId = active.id;
        const overColumnId = over.id;

        setKanbanData((prevKanbanData) => {
            const updatedColumns = { ...prevKanbanData.columns };
            const activeColumnIndex = prevKanbanData.columnOrder.findIndex((col) => col.id === activeColumnId);
            const overColumnIndex = prevKanbanData.columnOrder.findIndex(())
        })
    }

    return (
        <main className="w-full h-full">
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <div className="grid grid-flow-col auto-cols-auto grid-rows-1 gap-x-4">
                <DndContext onDragEnd={(event: DragEndEvent) => { console.log("DRAG END", event) }} onDragStart={(event: DragStartEvent) => { console.log("DRAG START", event) }}>
                    <SortableContext items={columnsID}>
                        {
                            kanbanData.columnOrder.map((columnID) => {
                                const columnData = kanbanData.columns[columnID as string];
                                return (
                                    <ColumnElement
                                        title={columnData.title}
                                        cardsList={columnData.cardsList}
                                        type={columnData.type}
                                        id={columnData.id}
                                        key={columnData.id}
                                        removeFunc={removeColumn} />
                                );
                            })
                        }
                    </SortableContext>
                </DndContext>
                <button onClick={addColumn} className='border-2 border-neutral-600 rounded-md p-2 bg-neutral-50 w-fit h-fit'>
                    add column
                </button>
            </div>
        </main>
    );
}


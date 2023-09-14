"use client";

//import { DragDropContext, Droppable, Draggable, DropResult } from "react-beautiful-dnd";
import { DndContext, useDroppable, useDraggable } from '@dnd-kit/core';
import { useEffect, useState } from 'react';
import { CSS } from '@dnd-kit/utilities';

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
    columns: {
        [key: string]: Column,
    },
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

function ColumnElement(props: Column) {
    const { isOver, setNodeRef } = useDroppable({
        id: props.id,
    });
    return (
        <div ref={setNodeRef} className="w-full w-64">
            <h1>{props.title}</h1>
            <h2>{props.type}</h2>
            <div>
                {props.cardsList.map((cardEl: Card, index: number) => <CardElement index={index} title={cardEl.title} id={cardEl.id} columnID={props.id} checklists={cardEl.checklists} description={cardEl.description} />)}
            </div>
        </div>
    );
}

const data: KanbanData = {
    columnOrder: ['column-0'],
    kanbanId: 'aaaaaaaaaa-bbbbbbbbbb-cccccccccc',
    columns: {
        'column-0': {
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
    },
};

export default function Page({ params }: { params: { id: string } }) {
    const [kanbanData, setKanbanData] = useState<KanbanData>(data);

    const onDragEndFunc = (result: any) => {
        const { destination, source, draggableId } = result;
        let updatedKanbanData: KanbanData = JSON.parse(JSON.stringify({ ...kanbanData }));

        if (!destination) return;
        if (destination.droppableId === source.droppableId && destination.index === source.index) return;

        const column: Column = updatedKanbanData.columns[source.droppableId as string];
        const cardsArr: Card[] = column.cardsList;
        const srcCardIdx = cardsArr.findIndex(card => card.id === column.cardsList[source.index].id);
        const dstCardIdx = cardsArr.findIndex(card => card.id === column.cardsList[destination.index].id);

        if (srcCardIdx !== -1 && dstCardIdx !== -1) {
            const movedCard = cardsArr.splice(srcCardIdx, 1)[0];
            cardsArr.splice(dstCardIdx, 0, movedCard);
        }
        console.log("updatedKanbanData:", updatedKanbanData);
        setKanbanData(updatedKanbanData as unknown as KanbanData);
        console.log("kanbanData:", kanbanData);
    };

    return (
        <main className="w-full h-full">
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <div className="grid grid-flow-col auto-cols-auto grid-rows-1 gap-x-4">
                <DndContext onDragEnd={() => { console.log("drag finished") }}>
                    {
                        kanbanData.columnOrder.map((columnID) => {
                            const columnData = kanbanData.columns[columnID as string];
                            return (
                                <ColumnElement
                                    title={columnData.title}
                                    cardsList={columnData.cardsList}
                                    type={columnData.type}
                                    id={columnData.id}
                                    key={columnData.id} />
                            );
                        })
                    }
                </DndContext>
            </div>
        </main>
    );
}


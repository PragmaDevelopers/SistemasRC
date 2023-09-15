"use client";

//import { DragDropContext, Droppable, Draggable, DropResult } from "react-beautiful-dnd";
import { DndContext, useDroppable, useDraggable, DragEndEvent, DragStartEvent, DragOverlay, useSensors, useSensor, PointerSensor, DragOverEvent } from '@dnd-kit/core';
import { MouseEventHandler, useEffect, useMemo, useState } from 'react';
import { CSS } from '@dnd-kit/utilities';
import { SortableContext, arrayMove, useSortable } from '@dnd-kit/sortable';
import { createPortal } from 'react-dom';
import { PlusCircleIcon, TrashIcon, XCircleIcon } from '@heroicons/react/24/outline';

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
    columnType: number,
    id: string,
    cardsList: Card[],
}

type KanbanData = {
    columns: Column[],
    kanbanId: string,
}


const data: KanbanData = {
    kanbanId: 'aaaaaaaaaa-bbbbbbbbbb-cccccccccc',
    columns: [
        {
            id: 'column-0',
            columnType: 0,
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

interface CardElementProps {
    card: Card,
    deleteCard: (columnID: string, cardID: string) => void;
}

function CardElement(props: CardElementProps) {
    const { card, deleteCard } = props;
    const { setNodeRef, attributes, listeners, transform, transition, isDragging } = useSortable({
        id: card.id,
        data: {
            type: 'CARD',
            card,
        },
    });

    const style = {
        transition,
        transform: CSS.Transform.toString(transform),
    };

    if (isDragging) {
        return (
            <div className='bg-neutral-300 border-neutral-800 rounded-md w-64 h-16 border-2'
                ref={setNodeRef} style={style} />
        );
    }


    return (
        <div className='my-2 bg-neutral-50 border-neutral-600 border-2 rounded-md p-2 relative'
            ref={setNodeRef} style={style} {...attributes} {...listeners}>
            <h1>{card.title}</h1>
            <p>{card.description}</p>
            <button className='absolute top-2 right-2' onClick={() => deleteCard(card.columnID, card.id)}>
                <XCircleIcon className='w-6 aspect-square' />
            </button>
        </div>
    );
}





interface ColumnContainerProps {
    column: Column;
    deleteColumn: (id: string) => void;
    updateColumnTitle: (id: string, title: string) => void;
    createCard: (columnID: string) => void;
    deleteCard: (columnID: string, cardID: string) => void;
}

function ColumnContainer(props: ColumnContainerProps) {
    const { column, deleteColumn, updateColumnTitle, createCard, deleteCard } = props;
    const [editMode, setEditMode] = useState<boolean>(false);
    const cardsIds = useMemo(() => { return column.cardsList.map((card: Card) => card.id) }, [column]);

    const handleRemove = () => {
        deleteColumn(column.id);
    }

    const { setNodeRef, attributes, listeners, transform, transition, isDragging } = useSortable({
        id: column.id,
        data: {
            type: 'COLUMN',
            column,
        },
        disabled: editMode,
    });

    const style = {
        transition,
        transform: CSS.Transform.toString(transform),
    };

    if (isDragging) {
        return (
            <div ref={setNodeRef} style={style} className='h-full w-64 bg-neutral-300 rounded-md border-2 border-neutral-600 content-[" "]'>
            </div>
        );
    }

    const handleCreateCard = () => {
        createCard(column.id);
    }

    return (
        <div className='relative w-64 h-full overflow-auto'
            ref={setNodeRef} style={style}>
            <div className='w-full bg-neutral-50 rounded-md border-2 border-neutral-950 p-2 mb-4 flex flex-row justify-between items-center'>
                <div
                    {...attributes} {...listeners} onClick={() => setEditMode(true)}>
                    {editMode ? <input
                        type='text'
                        autoFocus
                        onBlur={() => setEditMode(false)}
                        onKeyDown={(e: any) => {
                            if (e.key !== "Enter") return;
                            setEditMode(false);
                        }}
                        value={column.title}
                        onChange={(e: any) => updateColumnTitle(column.id, e.target.value)}
                        className='w-full bg-neutral-50 outline-none'
                    /> :
                        column.title}

                </div>
                <button onClick={handleRemove}>
                    <XCircleIcon className='w-6 aspect-square' />
                </button>
            </div>
            <div>
                <SortableContext items={cardsIds}>
                    {column.cardsList.map((card: Card) => {
                        return <CardElement card={card} deleteCard={deleteCard} />
                    })}
                </SortableContext>
            </div>
            <button onClick={handleCreateCard} className=' relative border-neutral-950 rounded-md border-2 p-2 flex w-full items-center justify-center mb-8'>
                <PlusCircleIcon className='w-8 aspect-square absolute top-1 left-2' />
                <h1 className='w-full text-center'>Add Card</h1>
            </button>
        </div>
    );
}

export default function Page({ params }: { params: { id: string } }) {
    const [kanbanData, setKanbanData] = useState<any>(data);
    const [activeColumn, setActiveColumn] = useState<Column | null>(null);
    const [activeCard, setActiveCard] = useState<Card | null>(null);
    const columnsId = useMemo(() => kanbanData.columns.map((col: Column) => col.id), [kanbanData]);
    const [showCreateCardForm, setShowCreateCardForm] = useState<boolean>(false);
    const [tempColumnID, setTempColumnID] = useState<string>("");
    const sensors = useSensors(useSensor(PointerSensor, {
        activationConstraint: {
            distance: 2,  // 2px
        }
    }));


    const createNewColumn = () => {
        const newColumn = {
            id: generateRandomString(),
            type: 0,
            title: `Column ${kanbanData.columns.length}`,
            cardsList: [],
        };

        setKanbanData((prevData: KanbanData) => ({
            ...prevData,
            columns: [...prevData.columns, newColumn],
        }));
    }

    const removeColumn = (columnIDToRemove: string) => {
        const updatedColumns = kanbanData.columns.filter(
            (column: Column) => column.id !== columnIDToRemove
        );

        // Update the Kanban data state with the updated columns array
        setKanbanData((prevData: KanbanData) => ({
            ...prevData,
            columns: updatedColumns,
        }));
    }


    const onDragStart = (event: DragStartEvent) => {
        console.log("DRAG START", event);
        if (event.active.data.current !== undefined) {
            if (event.active.data.current.type === "COLUMN") {
                setActiveColumn(event.active.data.current.column);
                return;
            }
        }

        if (event.active.data.current !== undefined) {
            if (event.active.data.current.type === "CARD") {
                setActiveCard(event.active.data.current.card);
                return;
            }
        }
    }

    const onDragEnd = (event: DragEndEvent) => {
        setActiveColumn(null);
        setActiveCard(null);

        const { active, over } = event;
        if (!over) return;

        const activeColumnID = active.id;
        const overColumnID = over.id;
        if (activeColumnID === overColumnID) return;

        setKanbanData((prevKanbanData: KanbanData) => {
            const activeColumnIndex = prevKanbanData.columns.findIndex((col: Column) => col.id === activeColumnID);
            const overColumnIndex = prevKanbanData.columns.findIndex((col: Column) => col.id === overColumnID);
            const newColumnsArray: Column[] = arrayMove(prevKanbanData.columns, activeColumnIndex, overColumnIndex);

            return {
                ...prevKanbanData,
                columns: newColumnsArray,
            };
        });

        console.log("DRAG END", event);
    }

    const onDragOver = (event: DragOverEvent) => {
        const { active, over } = event;
        if (!over) return;

        const activeID = active.id;
        const overID = over.id;
        if (activeID === overID) return;

        console.log("DRAG OVER", event);

        const isActiveCard = active.data.current?.type === "CARD";
        const isOverCard = over.data.current?.type === "CARD";

        if (isActiveCard && isOverCard) {
            setKanbanData((prevKanbanData: KanbanData) => {
                if (active.data.current?.card.columnID === over.data.current?.card.columnID) {
                    const targetColumn = prevKanbanData.columns.find((column) => column.id === active.data.current?.card.columnID);
                    if (!targetColumn) return prevKanbanData;

                    const activeCardIndex = targetColumn.cardsList.findIndex((card: Card) => card.id === activeID);
                    const overCardIndex = targetColumn.cardsList.findIndex((card: Card) => card.id === overID);

                    const newCardArray: Card[] = arrayMove(targetColumn.cardsList, activeCardIndex, overCardIndex);

                    const updatedColumn = {
                        ...targetColumn,
                        cardsList: newCardArray,
                    };

                    const updatedColumns = prevKanbanData.columns.map((column: Column) =>
                        column.id === active.data.current?.card.columnID ? updatedColumn : column
                    );

                    return {
                        ...prevKanbanData,
                        columns: updatedColumns,
                    };
                } else {
                    const sourceColumn = prevKanbanData.columns.find((column) => column.id === active.data.current?.card.columnID);
                    if (!sourceColumn) return prevKanbanData;
                    const destColumn = prevKanbanData.columns.find((col: Column) => col.id === over.data.current?.card.columnID);
                    if (!destColumn) return prevKanbanData;

                    const srcCardIndex = sourceColumn.cardsList.findIndex((card: Card) => card.id === activeID);
                    const destCardIndex = destColumn.cardsList.findIndex((card: Card) => card.id === overID);

                    const updatedSourceCardsList = sourceColumn.cardsList.filter((card) => card.id !== activeID);

                    const updatedSourceColumn = {
                        ...sourceColumn,
                        cardsList: updatedSourceCardsList,
                    };

                    const updatedColumns = prevKanbanData.columns.map((column: Column) =>
                        column.id === active.data.current?.card.columnID ? updatedSourceColumn : column
                    );

                    return {
                        ...prevData,
                        columns: updatedColumns,
                    };


                    const newSrcCardArray: Card[] = 
                }
            });
        }

    }



    const updateColumnTitle = (columnID: string, title: string) => {
        setKanbanData((prevKanbanData: KanbanData) => {
            const newColumns: Column[] = prevKanbanData.columns.map((col: Column) => {
                if (col.id !== columnID) return col;
                return { ...col, title: title };
            })
            return {
                ...prevKanbanData,
                columns: newColumns,
            }
        })
    };

    const createCard = (columnID: string) => {
        setTempColumnID(columnID);
        setShowCreateCardForm(true);
    };

    const createCardForm = (event: any) => {
        event.preventDefault();
        const cardTitle: string = event.target.title.value;
        const cardDescription: string = event.target.description.value;

        // Check if the card title is not empty before creating the card
        if (cardTitle.trim() !== "") {
            setKanbanData((prevData: KanbanData) => {
                const newCard: Card = {
                    id: generateRandomString(),
                    title: cardTitle,
                    columnID: tempColumnID,
                    description: cardDescription,
                    checklists: [],
                }

                const targetColumn = prevData.columns.find((column) => column.id === tempColumnID);
                if (!targetColumn) {
                    return prevData;
                }

                const updatedColumn = {
                    ...targetColumn,
                    cardsList: [...targetColumn.cardsList, newCard],
                };
                const updatedColumns = prevData.columns.map((column) =>
                    column.id === tempColumnID ? updatedColumn : column
                );

                console.log(newCard);

                return {
                    ...prevData,
                    columns: updatedColumns,
                };
            });
        }
        event.target.reset();
        setTempColumnID("");
        setShowCreateCardForm(false);
    };

    const deleteCard = (columnID: string, cardID: string) => {
        setKanbanData((prevData: KanbanData) => {
            const targetColumn = prevData.columns.find((column) => column.id === columnID);
            if (!targetColumn) {
                return prevData;
            }

            const updatedCardsList = targetColumn.cardsList.filter((card) => card.id !== cardID);

            const updatedColumn = {
                ...targetColumn,
                cardsList: updatedCardsList,
            };

            const updatedColumns = prevData.columns.map((column) =>
                column.id === columnID ? updatedColumn : column
            );

            return {
                ...prevData,
                columns: updatedColumns,
            };
        });
    };

    return (
        <main className="w-full h-full overflow-x-auto overflow-y-none">
            <div className={(showCreateCardForm ? 'flex ' : 'hidden ') + 'absolute top-0 left-0 w-full h-full z-20 justify-center items-center bg-neutral-950/50'}>
                <div className='w-[60%] h-[80%] bg-neutral-50 rounded-lg border-neutral-600 border-2'>
                    <form onSubmit={createCardForm}>
                        <input type='text' name='title' placeholder='Digite um nome' />
                        <textarea name='description' placeholder='Digite uma descrição'></textarea>
                        <button type='submit'>Create Card</button>
                    </form>
                    <button onClick={() => setShowCreateCardForm(false)}>Close</button>
                </div>
            </div>
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <DndContext sensors={sensors} onDragStart={onDragStart} onDragEnd={onDragEnd} onDragOver={onDragOver}>
                <div className="flex flex-row justify-start items-start gap-x-2 w-full h-full">
                    <SortableContext items={columnsId}>
                        {kanbanData.columns.map((col: Column) => <ColumnContainer
                            createCard={createCard}
                            deleteCard={deleteCard}
                            updateColumnTitle={updateColumnTitle}
                            key={col.id}
                            column={col}
                            deleteColumn={removeColumn} />)}
                    </SortableContext>
                    <button onClick={createNewColumn}>
                        Add Column
                    </button>
                </div>
                {createPortal(
                    <DragOverlay className='w-full h-full'>
                        {activeColumn && <ColumnContainer
                            deleteCard={deleteCard}
                            createCard={createCard}
                            updateColumnTitle={updateColumnTitle}
                            column={activeColumn}
                            deleteColumn={removeColumn} />}
                    </DragOverlay>,
                    document.body)}

            </DndContext>
        </main>
    );
}

function generateRandomString(): string {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const format = [10, 21];

    for (let i = 0; i < 32; i++) {
        // Insert hyphens at the specified positions
        if (format.includes(i)) {
            result += '-';
        } else {
            const randomIndex = Math.floor(Math.random() * characters.length);
            result += characters[randomIndex];
        }
    }

    return result;
}


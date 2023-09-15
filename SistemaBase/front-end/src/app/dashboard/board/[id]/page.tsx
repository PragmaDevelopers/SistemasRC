"use client";

//import { DragDropContext, Droppable, Draggable, DropResult } from "react-beautiful-dnd";
import { DndContext, useDroppable, useDraggable, DragEndEvent, DragStartEvent, DragOverlay, useSensors, useSensor, PointerSensor } from '@dnd-kit/core';
import { MouseEventHandler, useEffect, useMemo, useState } from 'react';
import { CSS } from '@dnd-kit/utilities';
import { SortableContext, arrayMove, useSortable } from '@dnd-kit/sortable';
import { createPortal } from 'react-dom';

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

interface ColumnContainerProps {
    column: Column;
    deleteColumn: (id: string) => void;
    updateColumnTitle: (id: string, title: string) => void;
    createCard: (columnID: string) => void;
}

function ColumnContainer(props: ColumnContainerProps) {
    const { column, deleteColumn, updateColumnTitle, createCard } = props;
    const [editMode, setEditMode] = useState<boolean>(false);

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
            <div ref={setNodeRef} style={style} className='h-full w-64 bg-neutral-300 rounded-md border-2 border-neutral-600'>
            </div>
        );
    }

    const handleCreateCard = () => {
        createCard(column.id,);
    }

    return (
        <div className='relative w-64 bg-neutral-50 rounded-md h-fit overflow-hidden'
            ref={setNodeRef} style={style}>
            <div className='w-full bg-neutral-100 border-b-2 border-neutral-400 p-2'
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
                /> :
                    column.title}

            </div>
            <button onClick={handleRemove}>
                delete
            </button>
            <div className='p-2'>
                {column.cardsList.map((card: Card) => {
                    return (
                        <div>
                            {card.title}
                        </div>
                    );
                })}
            </div>
            <button onClick={handleCreateCard}>
                Add Card
            </button>
        </div>
    );
}

export default function Page({ params }: { params: { id: string } }) {
    const [kanbanData, setKanbanData] = useState<any>(data);
    const [activeColumn, setActiveColumn] = useState<Column | null>(null);
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
        // Create a copy of the columns array without the specified column
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
    }

    const onDragEnd = (event: DragEndEvent) => {
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
        const cardTitle: string = event.target.title;
        const cardDescription: string = event.target.description;
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

            return {
                ...prevData,
                columns: updatedColumns,
            };
        });
        setTempColumnID("");
        setShowCreateCardForm(false);
    };

    return (
        <main className="w-full h-full">
            <div className={(showCreateCardForm ? 'flex ' : 'hidden ') + 'absolute top-0 w-screen h-screen z-20 justify-center items-center'}>
                <div>
                    <form onSubmit={createCardForm}>
                        <input type='text' name='title' placeholder='Nome' />
                        <textarea name='description'>Description</textarea>
                        <button type='submit'>Create Card</button>
                    </form>
                    <button onClick={() => setShowCreateCardForm(false)}>Close</button>
                </div>
            </div>
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <DndContext sensors={sensors} onDragStart={onDragStart} onDragEnd={onDragEnd}>
                <div className="grid grid-flow-col auto-cols-auto grid-rows-1 gap-x-2">
                    <SortableContext items={columnsId}>
                        {kanbanData.columns.map((col: Column) => <ColumnContainer
                            createCard={createCard}
                            updateColumnTitle={updateColumnTitle} key={col.id} column={col} deleteColumn={removeColumn} />)}
                    </SortableContext>
                    <button onClick={createNewColumn}>
                        Add Column
                    </button>
                </div>
                {createPortal(<DragOverlay>
                    {activeColumn && <ColumnContainer createCard={createCard} updateColumnTitle={updateColumnTitle} column={activeColumn} deleteColumn={removeColumn} />}
                </DragOverlay>, document.body)}
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


"use client";

import { DndContext, useDroppable, useDraggable, DragEndEvent, DragStartEvent, DragOverlay, useSensors, useSensor, PointerSensor, DragOverEvent } from '@dnd-kit/core';
import { useEffect, useMemo, useState } from 'react';
import { CSS } from '@dnd-kit/utilities';
import { SortableContext, arrayMove, useSortable } from '@dnd-kit/sortable';
import { createPortal } from 'react-dom';
import { MinusCircleIcon, PlusCircleIcon, TrashIcon, XCircleIcon } from '@heroicons/react/24/outline';

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

interface CardElementProps {
    card: Card,
    deleteCard: (columnID: string, cardID: string) => void;
    setShowCreateCardForm: any;
    setTempCard: any;
    setIsEdition: any;
    setTempColumnID: any;
}

function CardElement(props: CardElementProps) {
    const { card, deleteCard, setShowCreateCardForm, setTempCard, setIsEdition, setTempColumnID } = props;
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
            <div className='bg-neutral-300 dark:bg-neutral-700 dark:border-neutral-50 border-neutral-950 rounded-md w-64 h-16 border-2'
                ref={setNodeRef} style={style} />
        );
    }

    const editCard = () => {
        setTempCard(card as Card);
        setTempColumnID(card.columnID);
        setIsEdition(true);
        setShowCreateCardForm(true);
    }

    return (
        <div className='my-2 dark:bg-neutral-950 dark:border-neutral-50 bg-neutral-50 border-neutral-950 border-2 rounded-md relative'
            ref={setNodeRef} style={style} {...attributes} {...listeners}>
            <div className='p-2 w-full h-full' onClick={editCard}>
                <h1>{card.title}</h1>
                <p>{card.description}</p>
            </div>
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
    setShowCreateCardForm: any;
    setTempCard: any;
    setIsEdition: any;
    setTempColumnID: any;
}

function ColumnContainer(props: ColumnContainerProps) {
    const { column, deleteColumn, updateColumnTitle, createCard, deleteCard, setShowCreateCardForm, setTempCard, setIsEdition, setTempColumnID } = props;
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
            <div ref={setNodeRef} style={style} className='h-full w-64 bg-neutral-300 rounded-md border-2 border-neutral-950 dark:bg-neutral-700 dark:border-neutral-50 content-[" "]'>
            </div>
        );
    }

    const handleCreateCard = () => {
        createCard(column.id);
    }

    return (
        <div className='relative w-64 h-full overflow-auto'
            ref={setNodeRef} style={style} {...attributes} {...listeners} >
            <div className='w-full bg-neutral-50 rounded-md border-2 border-neutral-950 p-2 mb-4 flex flex-row justify-between items-center dark:bg-neutral-950 dark:border-neutral-50'>
                <div
                    onClick={() => setEditMode(true)}>
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
                        className='w-full bg-neutral-50 dark:bg-neutral-950 outline-none'
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
                        return <CardElement setTempColumnID={setTempColumnID} setTempCard={setTempCard} setShowCreateCardForm={setShowCreateCardForm} card={card} deleteCard={deleteCard} setIsEdition={setIsEdition} />
                    })}
                </SortableContext>
            </div>
            <button onClick={handleCreateCard} className=' relative border-neutral-950 rounded-md border-2 p-2 flex w-full items-center justify-center dark:border-neutral-50'>
                <PlusCircleIcon className='w-8 aspect-square absolute top-1 left-2' />
                <h1 className='w-full text-center'>Add Card</h1>
            </button>
        </div>
    );
}

interface CreateEditCardProps {
    showCreateCardForm: boolean,
    createCardForm: any,
    card: Card,
    updateListTitle: any,
    handleRemoveInput: any,
    handleRemoveList: any
    handleAddList: any,
    handleAddInput: any,
    setShowCreateCardForm: any,
    handleInputChange: any,
    handleToggleCheckbox: any,
    isEdition: boolean,
}

function CreateEditCard(props: CreateEditCardProps) {
    const { setShowCreateCardForm,
        showCreateCardForm,
        createCardForm,
        card,
        handleAddList,
        handleRemoveInput,
        handleRemoveList,
        updateListTitle,
        handleAddInput,
        handleInputChange,
        handleToggleCheckbox,
        isEdition } = props;

    const handleCreateCardForm = (event: any) => {
        createCardForm(event, isEdition);
    }

    return (
        <div className={(showCreateCardForm ? 'flex ' : 'hidden ') + 'absolute top-0 left-0 w-full h-full z-20 justify-center items-center bg-neutral-950/50 dark:bg-black/50'}>
            <div className='relative w-[60%] h-[80%] dark:bg-neutral-950 dark:border-neutral-50 bg-neutral-50 rounded-lg border-neutral-950 border-2 flex justify-center items-center'>
                <h1 className='absolute top-2 w-full text-center'>Card Creation</h1>
                <form onSubmit={handleCreateCardForm} className='w-[80%] h-[85%] mt-[5%] relative'>
                    <div className='w-full h-[85%] overflow-y-auto pb-4'>
                        <div className='flex my-2'>
                            <label htmlFor='CardTitle' className='mr-2'>Titulo:</label>
                            <input className='bg-neutral-50 dark:bg-neutral-950' id="CardTitle" type='text' defaultValue={card.title} name='title' placeholder='Digite um titulo' />
                        </div>
                        <div className='flex flex-col my-2 border-2 rounded-md border-neutral-950 p-2 outline-none dark:border-neutral-50'>
                            <label htmlFor='CardDescription' className='mb-2'>Descrição</label>
                            <textarea className='resize-none w-full h-32 bg-neutral-50 dark:bg-neutral-950' id="CardDescription" defaultValue={card.description} name='description' placeholder='Digite uma descrição'></textarea>
                        </div>
                        <div>
                            {card.checklists?.map((list: CheckList, listIndex: number) => (
                                <div key={listIndex} className='rounded-md border-2 border-neutral-200 p-2 w-80 h-fit my-2 dark:border-neutral-700'>
                                    <div className='flex items-center mb-4'>
                                        <input type='text' className='shrink-0 mr-2 p-0.5 bg-neutral-50 outline-none w-64 dark:bg-neutral-950' value={list.name} onChange={(e) => updateListTitle(listIndex, e.target.value)} />
                                        <button
                                            type="button"
                                            onClick={() => handleRemoveList(listIndex)}
                                        >
                                            <MinusCircleIcon className='w-6 aspect-square' />
                                        </button>
                                    </div>
                                    {list.items?.map((inputValue: CheckListItem, inputIndex: number) => (
                                        <div key={inputIndex} className='flex items-center my-2'>
                                            <input
                                                type="checkbox"
                                                checked={inputValue.completed}
                                                onChange={() => handleToggleCheckbox(listIndex, inputIndex)}
                                            />
                                            <input
                                                className='dark:bg-neutral-900 border-2 rounded-md bg-neutral-100 mr-2 p-0.5 w-64'
                                                type="text"
                                                value={inputValue.name}
                                                placeholder='Adicionar Tarefa'
                                                onChange={(e) =>
                                                    handleInputChange(listIndex, inputIndex, e.target.value)
                                                }
                                            />
                                            <button
                                                type="button"
                                                onClick={() => handleRemoveInput(listIndex, inputIndex)}
                                            >
                                                <MinusCircleIcon className='w-6 aspect-square' />
                                            </button>
                                        </div>
                                    ))}
                                    <button type="button" className="flex items-center justify-center w-full" onClick={() => handleAddInput(listIndex)}>
                                        <h1 className='mr-2'>Nova Tarefa</h1>
                                        <PlusCircleIcon className='w-6 aspect-square' />
                                    </button>
                                </div>
                            ))}
                            <button type="button" onClick={handleAddList} className='my-2 rounded-md w-80 p-2 border-neutral-950 dark:border-neutral-50 border-2 flex justify-center items-center'>
                                <h1 className="mr-2">Nova Lista</h1>
                                <PlusCircleIcon className='w-6 aspect-square' />
                            </button>
                        </div>
                    </div>
                    <div className='w-full absolute bottom-0 flex justify-center items-center'>
                        <button type='submit' className='dark:border-neutral-50 w-fit p-2 border-2 border-neutral-950 rounded-md'>Create Card</button>
                    </div>
                </form>
                <button onClick={() => setShowCreateCardForm(false)}><XCircleIcon className='w-8 aspect-square absolute top-2 right-2' /></button>
            </div>
        </div>
    );
}

export default function Page({ params }: { params: { id: string } }) {
    const [tempDragState, setTempDragState] = useState<any>(null);
    const [kanbanData, setKanbanData] = useState<any>({});
    const [activeColumn, setActiveColumn] = useState<Column | null>(null);
    const [activeCard, setActiveCard] = useState<Card | null>(null);
    const columnsId = useMemo(() => {
        if (kanbanData && kanbanData.columns && kanbanData.columns.length > 0) {
            return kanbanData.columns.map((col: any) => col.id);
        } else {
            return [];
        }
    }, [kanbanData]);
    const [showCreateCardForm, setShowCreateCardForm] = useState<boolean>(false);
    const [tempColumnID, setTempColumnID] = useState<string>("");
    const [lists, setLists] = useState([{ title: 'New List', inputs: [{ name: '', checked: false }], id: generateRandomString() }]);
    const [tempCard, setTempCard] = useState<any>({});
    const [isEdition, setIsEdition] = useState<boolean>(false);

    const sensors = useSensors(useSensor(PointerSensor, {
        activationConstraint: {
            distance: 2,  // 2px
        }
    }));

    useEffect(() => {
        fetch(`http://localhost:8080/api/dashboard/column/getall/${params.id}`).then(response => response.json()).then(data => {

        })
    }, []);


    const createNewColumn = () => {
        if (kanbanData.columns !== undefined) {
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
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    columnId: newColumn.id,
                    title: newColumn.title,
                    columnType: newColumn.type,
                    kanbanId: params.id,
                }),
            };

            fetch(`http://localhost:8080/api/dashboard/column/create/${params.id}`, requestOptions).then(response => response.json).then(data => console.log(data))
        } else {
            const newColumn = {
                id: generateRandomString(),
                type: 0,
                title: 'Column 0',
                cardsList: [],
            };
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    id: newColumn.id,
                    title: newColumn.title,
                    columnType: newColumn.type,
                    kanbanId: params.id,
                }),
            };

            fetch(`http://localhost:8080/api/dashboard/column/create/${params.id}`, requestOptions).then(response => response.json).then(data => console.log(data))

            setKanbanData((prevData: KanbanData) => ({
                ...prevData,
                columns: [newColumn],
            }));
        }
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
        //console.log("DRAG START", event);
        setTempDragState(event);

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

        //console.log("ON DRAG END EVENT", event);
        if (active.data.current?.type === "COLUMN") {
            //console.log("ACTIVE COLUMN");
            setKanbanData((prevKanbanData: KanbanData) => {
                const activeColumnIndex = prevKanbanData.columns.findIndex((col: Column) => col.id === activeColumnID);
                const overColumnIndex = prevKanbanData.columns.findIndex((col: Column) => col.id === overColumnID);
                const newColumnsArray: Column[] = arrayMove(prevKanbanData.columns, activeColumnIndex, overColumnIndex);

                return {
                    ...prevKanbanData,
                    columns: newColumnsArray,
                };
            });
        } else {
            //console.log("ACTIVE CARD", active.data.current?.type);
            if (over.data.current?.type === "COLUMN") {
                //console.log("OVER COLUMN");
                setKanbanData((prevKanbanData: KanbanData) => {
                    // Drop on other column
                    const cardEl: Card = active.data.current?.card;
                    const destCol: Column = over.data.current?.column;
                    const srcCol: Column | undefined = prevKanbanData.columns.find((col: Column) => col.id === active.data.current?.card.columnID);
                    if (!srcCol) return;

                    const updatedCardsList = srcCol.cardsList.filter((card) => card.id !== cardEl.id);

                    const updatedColumn = {
                        ...srcCol,
                        cardsList: updatedCardsList,
                    };

                    const newCard: Card = {
                        ...cardEl,
                        columnID: destCol.id,
                    }

                    const resultDestCol: Column = {
                        ...destCol,
                        cardsList: [...destCol.cardsList, newCard],
                    }

                    const updatedSrcColumns: Column[] = prevKanbanData.columns.map((column: Column) =>
                        column.id === updatedColumn.id ? updatedColumn : column
                    );

                    const updatedColumns: Column[] = updatedSrcColumns.map((col: Column) => col.id === resultDestCol.id ? resultDestCol : col);

                    return {
                        ...prevKanbanData,
                        columns: updatedColumns,
                    };

                    // drop on card in other column

                })
            } else if (over.data.current?.type === "CARD") {
                //console.log("OVER CARD");
                if (Object.keys(active.data.current as any).length !== 0) {
                    //console.log("CURRENT NOT EMPTY", event);
                    setKanbanData((prevKanbanData: KanbanData) => {
                        const cardEl: Card = active.data.current?.card;
                        const destCol: Column | undefined = prevKanbanData.columns.find((col: Column) => col.id === over.data.current?.card.columnID);
                        const srcCol: Column | undefined = prevKanbanData.columns.find((col: Column) => col.id === active.data.current?.card.columnID);
                        if (!srcCol) return;
                        if (destCol === undefined) return;

                        //console.log(destCol, srcCol);
                        const updatedCardsList = srcCol.cardsList.filter((card) => card.id !== cardEl.id);

                        const updatedColumn = {
                            ...srcCol,
                            cardsList: updatedCardsList,
                        };

                        const newCard: Card = {
                            ...cardEl,
                            columnID: destCol.id,
                        }
                        const resultDestCol: Column = {
                            ...destCol,
                            cardsList: [...destCol.cardsList, newCard],
                        }

                        const updatedSrcColumns: Column[] = prevKanbanData.columns.map((column: Column) =>
                            column.id === updatedColumn.id ? updatedColumn : column
                        );

                        const updatedColumns: Column[] = updatedSrcColumns.map((col: Column) => col.id === resultDestCol.id ? resultDestCol : col);

                        return {
                            ...prevKanbanData,
                            columns: updatedColumns,
                        };

                    });
                } else {
                    //console.log("CURRENT EMPTY");
                    setKanbanData((prevKanbanData: KanbanData) => {
                        const tempEndDragState: DragEndEvent = tempDragState as DragEndEvent;
                        const cardEl: Card = tempEndDragState.active.data.current?.card;
                        const destCol: Column | undefined = prevKanbanData.columns.find((col: Column) => col.id === over.data.current?.card.columnID);
                        const srcCol: Column | undefined = prevKanbanData.columns.find((col: Column) => col.id === tempEndDragState.active.data.current?.card.columnID);
                        if (!srcCol) return;
                        if (destCol === undefined) return;

                        const updatedCardsList = srcCol.cardsList.filter((card) => card.id !== cardEl.id);

                        const updatedColumn = {
                            ...srcCol,
                            cardsList: updatedCardsList,
                        };

                        const newCard: Card = {
                            ...cardEl,
                            columnID: destCol.id,
                        }
                        const resultDestCol: Column = {
                            ...destCol,
                            cardsList: [...destCol.cardsList, newCard],
                        }

                        const updatedSrcColumns: Column[] = prevKanbanData.columns.map((column: Column) =>
                            column.id === updatedColumn.id ? updatedColumn : column
                        );

                        const updatedColumns: Column[] = updatedSrcColumns.map((col: Column) => col.id === resultDestCol.id ? resultDestCol : col);

                        return {
                            ...prevKanbanData,
                            columns: updatedColumns,
                        };

                    });
                }
            }
        }

        //console.log("DRAG END", event);
    }

    const onDragOver = (event: DragOverEvent) => {
        const { active, over } = event;
        if (!over) return;

        const activeID = active.id;
        const overID = over.id;
        if (activeID === overID) return;

        //console.log("DRAG OVER", event);

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

                    //const srcCardIndex = sourceColumn.cardsList.findIndex((card: Card) => card.id === activeID);
                    //const destCardIndex = destColumn.cardsList.findIndex((card: Card) => card.id === overID);

                    const updatedSourceCardsList = sourceColumn.cardsList.filter((card) => card.id !== activeID);

                    const updatedSourceColumn = {
                        ...sourceColumn,
                        cardsList: updatedSourceCardsList,
                    };

                    const updatedColumns = prevKanbanData.columns.map((column: Column) =>
                        column.id === active.data.current?.card.columnID ? updatedSourceColumn : column
                    );

                    return {
                        ...prevKanbanData,
                        columns: updatedColumns,
                    };
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
        setTempCard({
            id: generateRandomString(),
            title: "",
            columnID: columnID,
            description: "",
            checklists: [],
        } as Card);
        setIsEdition(false);
        setShowCreateCardForm(true);
    };

    const createCardForm = (event: any, isEdition: boolean) => {
        event.preventDefault();
        const cardTitle: string = event.target.title.value;
        const cardDescription: string = event.target.description.value;

        // Check if the card title is not empty before creating the card
        if (cardTitle.trim() !== "") {
            setKanbanData((prevData: KanbanData) => {
                const newCard: Card = {
                    ...tempCard,
                    title: cardTitle,
                    description: cardDescription,
                }
                const targetColumn = prevData.columns.find((column) => column.id === tempColumnID);
                if (!targetColumn) {
                    return prevData;
                }

                if (!isEdition) {
                    console.log(`CARD ${newCard.id} CREATED.`);
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
                } else {
                    console.log(`CARD ${newCard.id} EDITED.`);
                    const cardIndex = targetColumn.cardsList.findIndex((card: Card) => card.id === newCard.id);
                    if (cardIndex !== -1) {
                        const updatedColumnCardList = targetColumn.cardsList.map((card: Card) => card.id === newCard.id ? newCard : card)
                        console.log(updatedColumnCardList);
                        const updatedColumn = {
                            ...targetColumn,
                            cardsList: updatedColumnCardList,
                        };

                        const updatedColumns = prevData.columns.map((column) =>
                            column.id === tempColumnID ? updatedColumn : column
                        );

                        return {
                            ...prevData,
                            columns: updatedColumns,
                        };
                    }
                }
            });
        }
        event.target.reset();
        setTempColumnID("");
        setTempCard({
            id: generateRandomString(),
            title: "",
            columnID: "",
            description: "",
            checklists: [],
        } as Card);
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


    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                          CHECKLISTS LOGIC                             //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////

    const handleInputChange = (listIndex: any, inputIndex: any, value: any) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists[listIndex].items[inputIndex].name = value;
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    };

    const updateListTitle = (listIndex: any, value: string) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists[listIndex].name = value;
            console.log(newChecklists[listIndex].name, listIndex);
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    }

    const handleAddList = () => {
        const checklistId = generateRandomString();
        setTempCard((prevCard: Card) => ({
            ...prevCard,
            checklists: [
                ...prevCard.checklists,
                {
                    name: 'New Checklist',
                    items: [{ name: '', completed: false, checklistId: checklistId }],
                    id: checklistId,
                },
            ],
        }) as Card);
    };

    const handleAddInput = (listIndex: any) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists[listIndex].items.push({ name: '', completed: false, checklistId: newChecklists[listIndex].id } as CheckListItem);
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    };

    const handleRemoveInput = (listIndex: any, inputIndex: any) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists[listIndex].items.splice(inputIndex, 1);
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    };

    const handleRemoveList = (listIndex: any) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists.splice(listIndex, 1);
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    };

    const handleToggleCheckbox = (listIndex: any, itemIndex: any) => {
        setTempCard((prevCard: Card) => {
            const newChecklists = [...prevCard.checklists];
            newChecklists[listIndex].items[itemIndex].completed = !newChecklists[listIndex].items[itemIndex].completed;
            return {
                ...prevCard,
                checklists: newChecklists,
            } as Card;
        });
    };

    return (
        <main className="w-full h-full overflow-x-auto overflow-y-hidden shrink-0">
            <CreateEditCard
                showCreateCardForm={showCreateCardForm}
                setShowCreateCardForm={setShowCreateCardForm}
                card={tempCard as Card}
                createCardForm={createCardForm}
                updateListTitle={updateListTitle}
                handleInputChange={handleInputChange}
                handleAddList={handleAddList}
                handleAddInput={handleAddInput}
                handleRemoveList={handleRemoveList}
                handleRemoveInput={handleRemoveInput}
                handleToggleCheckbox={handleToggleCheckbox}
                isEdition={isEdition}
            />
            <div className="">
                <h1>{params.id}</h1>
            </div>
            <DndContext sensors={sensors} onDragStart={onDragStart} onDragEnd={onDragEnd} onDragOver={onDragOver}>
                <div className="flex flex-row justify-start items-start gap-x-2 w-full h-[95%] overflow-auto shrink-0">
                    <SortableContext items={columnsId}>
                        {kanbanData.columns?.map((col: Column) => <ColumnContainer
                            createCard={createCard}
                            deleteCard={deleteCard}
                            updateColumnTitle={updateColumnTitle}
                            key={col.id}
                            column={col}
                            deleteColumn={removeColumn}
                            setShowCreateCardForm={setShowCreateCardForm}
                            setTempCard={setTempCard}
                            setIsEdition={setIsEdition}
                            setTempColumnID={setTempColumnID} />)}
                    </SortableContext>
                    <button className='w-64 h-full rounded-md border-2 border-neutral-950 flex flex-col justify-center items-center dark:border-neutral-50' onClick={createNewColumn}>
                        <h1 className='mb-2'>Add Column</h1>
                        <PlusCircleIcon className='w-8 aspect-square' />
                    </button>
                </div>
                {createPortal(
                    <DragOverlay className='w-full h-full'>
                        {activeColumn && <ColumnContainer
                            deleteCard={deleteCard}
                            createCard={createCard}
                            updateColumnTitle={updateColumnTitle}
                            column={activeColumn}
                            deleteColumn={removeColumn}
                            setTempCard={setTempCard}
                            setShowCreateCardForm={setShowCreateCardForm}
                            setIsEdition={setIsEdition}
                            setTempColumnID={setTempColumnID} />}
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


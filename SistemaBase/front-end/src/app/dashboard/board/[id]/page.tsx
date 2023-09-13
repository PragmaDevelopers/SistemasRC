"use client";

import { useState } from "react";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd";


function genRandInt(): number {
    const min = 10000000;
    const max = 99999999;
    const randomInteger = Math.floor(Math.random() * (max - min + 1)) + min;
    return randomInteger;
}


type CheckList = {
    name: string,
    id: number,
    items: CheckListItem[],
}

type CheckListItem = {
    name: string,
    completed: boolean,
    checklistId: number,
}

type Card = {
    title: string,
    id: number,
    columnID: number,
    description: string,
    checklists: CheckList[],
}

type Column = {
    title: string,
    type: number,
    id: number,
    cardsList: Card[],
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

function CardElement(props: Card) {
    return (
        <div className="w-full bg-neutral-50 rounded-md my-2 p-2">
            <h1>{props.title}</h1>
            <p>{props.description}</p>
            <div>
                {props.checklists.map((checklist: CheckList) => {
                    return (
                        <CheckListElement name={checklist.name} id={checklist.id} items={checklist.items} key={checklist.id} />
                    );
                })}
            </div>
        </div>
    );
}

function ColumnElement(props: Column) {
    return (
        <div className="w-full w-64">
            <h1>{props.title}</h1>
            <h2>{props.type}</h2>
            <div>
                {props.cardsList.map((cardEl: Card) => {
                    return (
                        <CardElement title={cardEl.title} id={cardEl.id} columnID={props.id} checklists={cardEl.checklists} description={cardEl.description} />
                    );
                })}
            </div>
        </div>
    );
}

const data: Column[] = [
    {
        id: 0,
        type: 0,
        title: "Column 00",
        cardsList: [
            {
                title: "Card 00",
                id: 0,
                columnID: 0,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 01",
                id: 1,
                columnID: 0,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 02",
                id: 2,
                columnID: 0,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
        ]
    },
    {
        id: 1,
        type: 0,
        title: "Column 01",
        cardsList: [
            {
                title: "Card 00",
                id: 0,
                columnID: 1,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 01",
                id: 1,
                columnID: 1,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 02",
                id: 2,
                columnID: 1,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
        ]
    },
    {
        id: 2,
        type: 1,
        title: "Column 02",
        cardsList: [
            {
                title: "Card 00",
                id: 0,
                columnID: 2,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 01",
                id: 1,
                columnID: 2,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
            {
                title: "Card 02",
                id: 2,
                columnID: 2,
                description: "Example Card",
                checklists: [
                    {
                        name: "CheckList 00",
                        id: 0,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 0
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 0
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 0
                            },

                        ]
                    },
                    {
                        name: "CheckList 01",
                        id: 1,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 1
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 1
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 1
                            },

                        ]
                    },
                    {
                        name: "CheckList 02",
                        id: 2,
                        items: [
                            {
                                name: "Item 00",
                                completed: false,
                                checklistId: 2
                            },
                            {
                                name: "Item 01",
                                completed: true,
                                checklistId: 2
                            },

                            {
                                name: "Item 02",
                                completed: false,
                                checklistId: 2
                            },

                        ]
                    },
                ],
            },
        ]
    },
];

export default function Page({ params }: { params: { id: string } }) {
    return (
        <main className="w-full h-full">
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <div className="grid grid-flow-col auto-cols-auto grid-rows-1 gap-x-4">
                {data.map((column: Column) => {
                    return (
                        <ColumnElement title={column.title} cardsList={column.cardsList} type={column.type} id={column.id} key={column.id} />
                    );
                })}
            </div>
        </main>
    );
}

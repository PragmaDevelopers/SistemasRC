"use client";

import '@mdxeditor/editor/style.css';
import { toolbarPlugin } from '@mdxeditor/editor/plugins/toolbar';
import {
    headingsPlugin,
    listsPlugin,
    quotePlugin,
    thematicBreakPlugin,
    linkPlugin,
    linkDialogPlugin,
    imagePlugin,
    tablePlugin,
    markdownShortcutPlugin,
    UndoRedo,
    BoldItalicUnderlineToggles,
    BlockTypeSelect,
    InsertImage,
    InsertTable,
    ListsToggle,
    CreateLink,
    ButtonOrDropdownButton,
    directivesPluginHooks,
    MDXEditorMethods,
    MDXEditor,
} from "@mdxeditor/editor";

import {
    RichEditorProps
} from '@/app/interfaces/KanbanInterfaces';
import { Bars3Icon } from '@heroicons/react/24/solid';
import { useRef, useState } from 'react';

function AlignTextMDXToolbarItem() {
    const insertDirective = directivesPluginHooks.usePublisher('insertDirective');
    const items = [
        { value: 'LEFT', label: 'Align Left' },
        { value: 'CENTER', label: 'Align Center' },
        { value: 'RIGHT', label: 'Align Right' },
    ]
    return (
        <ButtonOrDropdownButton
            title='Align Text'
            onChoose={(alignName) => {
                insertDirective({
                    type: 'containerDirective',
                    name: alignName,
                });
            }}
            items={items}
        >
            <Bars3Icon className='aspect-square w-6' />
        </ButtonOrDropdownButton>
    );
}

interface TestProps extends RichEditorProps {
    setMdText: any;
}

function RichEditor(props: TestProps) {
    const editorRef = useRef<MDXEditorMethods>(null);
    const getMd = () => {
        console.log("GETTING MD");
        const md = editorRef.current?.getMarkdown();
        console.log(md);
        props.setMdText(md);
    }
    const resetMd = () => {
        console.log("RESETTING MD");
        editorRef.current?.setMarkdown("");
    }
    return (
        <div>
            <button className='my-2' onClick={resetMd}>Reset Markdown</button>
            <button className='my-2' onClick={getMd}>Get Markdown</button>
            <MDXEditor
                onChange={console.log}
                className="MDXEditor"
                ref={editorRef}
                markdown={props.markdown != undefined ? props?.markdown : ""}
                plugins={[
                    headingsPlugin(),
                    listsPlugin(),
                    quotePlugin(),
                    thematicBreakPlugin(),
                    linkPlugin(),
                    linkDialogPlugin(),
                    imagePlugin(),
                    tablePlugin(),
                    markdownShortcutPlugin(),
                    toolbarPlugin({
                        toolbarContents: () => (<>
                            <UndoRedo />
                            <BlockTypeSelect />
                            <BoldItalicUnderlineToggles />
                            <InsertImage />
                            <InsertTable />
                            <ListsToggle />
                            <CreateLink />
                            <AlignTextMDXToolbarItem />
                        </>
                        )
                    }),
                ]}

            />
        </div>
    );
}

export default function Page() {
    const [mdText, setMdText] = useState<any>("");
    return (
        <main className="w-full h-full bg-neutral-50">
            <RichEditor markdown={mdText} setMdText={setMdText} />
            <div className='p-2 border-[1px] border-neutral-200 bg-neutral-100 my-2 shadow-inner'>
                <p>{mdText}</p>
            </div>
        </main>
    );
}


import { NestedLexicalEditor,jsxPluginHooks,JsxComponentDescriptor } from "@mdxeditor/editor";

export const jsxComponentDescriptors: JsxComponentDescriptor[] = [
  {
    name: 'MyLeaf',
    kind: 'text',
    source: './external',
    props: [{ name: 'type', type: 'string' }],
    hasChildren: true,
    Editor: () => {
      return (
        <p style={{ textAlign: "center" }}></p>
      )
    }
  }
]

export const InsertMyLeaf = () => {
  const insertJsx = jsxPluginHooks.usePublisher('insertJsx')
  return (
    <button
      onClick={() =>
        insertJsx({
          name: 'MyLeaf',
          kind: 'text',
          props: { }
        })
      }
    >
      Leaf
    </button>
  )
}
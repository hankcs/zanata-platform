jest.disableAutomock()

import React from 'react'
import ReactDOMServer from 'react-dom/server'
import SuggestionSources from '../../app/components/SuggestionSources'
import SuggestionContents from '../../app/components/SuggestionContents'
import SuggestionSourceDetails from '../../app/components/SuggestionSourceDetails'

describe('SuggestionSourcesTest', () => {
  it('SuggestionSources markup', () => {
    const actual = ReactDOMServer.renderToStaticMarkup(
      <SuggestionSources
        showDiff={false}
        suggestion={{
          matchDetails: [
            {
              type: 'IMPORTED_TM',
              transMemorySlug: 'patterson'
            }
          ],
          sourceContents: [
            'There was movement at the station',
            'for the word had passed around'
          ]
        }}/>
    )

    const expected = ReactDOMServer.renderToStaticMarkup(
      <div className="TransUnit-panel TransUnit-source">
        <SuggestionContents
          plural={true}
          contents={[
            'There was movement at the station',
            'for the word had passed around'
          ]}
          compareTo={undefined}/>
        <SuggestionSourceDetails
          suggestion={{
            matchDetails: [
              {
                type: 'IMPORTED_TM',
                transMemorySlug: 'patterson'
              }
            ],
            sourceContents: [
              'There was movement at the station',
              'for the word had passed around'
            ]
          }}/>
      </div>
    )
    expect(actual).toEqual(expected)
  })
})

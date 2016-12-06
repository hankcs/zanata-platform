/**
 * A react-bootstrap Panel that displays details for a LOCAL_PROJECT suggestion
 * match.
 */

import React, { Component, PropTypes } from 'react'
import { Icon, Row } from 'zanata-ui'
import { Panel, Label } from 'react-bootstrap'
import SuggestionUpdateMessage from '../../components/SuggestionUpdateMessage'
import { matchType } from '../../utils/suggestion-util'

class LocalProjectDetailPanel extends Component {

  fullDocName (path, name) {
    return (path || '') + name
  }

  matchHeader (matchDetail) {
    const { documentName, documentPath } = matchDetail
    const fullDocName = this.fullDocName(documentPath, documentName)
    return (
      <div className="transUnit-details">
        <ul className="u-listInline u-sMB-1-4">
          <li title={fullDocName}>
            <Row>
              <Icon name="document"
                size="1"
                theme={{
                  base: {
                    px: 'Px(rh)'
                  }
                }} /> {fullDocName}
            </Row>
          </li>
        </ul>
      </div>
    )
  }

  matchProperties (matchDetail) {
    const {
      documentName,
      documentPath,
      projectId,
      projectName,
      version } = matchDetail

    // FIXME example has document name shortened with an ellipsis in the middle
    //       when it is wider than the available space.
    const fullDocName = this.fullDocName(documentPath, documentName)

    // FIXME use standard styles when they are available
    // these values are based on variables in TransUnit/index.css but are
    // somehow transformed when the CSS is compiled, so values are taken from
    // the bundle file.
    const styleByMatchType = {
      // --TransUnit-color-success
      translated: {'backgroundColor': '#5cca7b'},
      // --TransUnit-color-highlight
      approved: {'backgroundColor': '#03a6d7'}
    }

    const labelTextByMatchType = {
      translated: 'Translated',
      approved: 'Approved'
    }

    const currentMatchType = matchType(matchDetail)

    return (
      <div className="transUnit-details">
        <ul className="u-listInline u-sMB-1-4">
          <li title={projectId}>
            <Row>
              <Icon name="project" size="1" /> {projectName}
            </Row>
          </li>
          <li>
            <Row>
              <Icon name="version" size="1" /> {version}
            </Row>
          </li>
          <li title={fullDocName}>
            <Row>
              <Icon name="document" size="1" /> {fullDocName}
            </Row>
          </li>
          <li>
            <Label style={styleByMatchType[currentMatchType]}>
              {labelTextByMatchType[currentMatchType]}</Label>
          </li>
        </ul>
      </div>
    )
  }

  render () {
    const {
      matchDetail,
      /* The collected ...props are used to make sure the returned Panel will
       * work properly with the enclosing PanelGroup/Accordion. If the props
       * are not passed through, panels do not work properly in a group. */
      ...props } = this.props
    const {
      lastModifiedBy,
      lastModifiedDate,
      sourceComment,
      targetComment } = matchDetail

    const lastChanged = new Date(lastModifiedDate)
    const currentMatchType = matchType(matchDetail)

    return (
      <Panel
        header={this.matchHeader(matchDetail)}
        {...props}
        bsStyle="info"
      >
        {this.matchProperties(matchDetail)}
        <SuggestionUpdateMessage
          user={lastModifiedBy || 'Anonymous'}
          lastChanged={lastChanged}
          matchType={currentMatchType}
            />
        <hr />
        <span className="comment-box">
          <h4 className="list-group-item-heading">Comments</h4>
          <ul className="list-inline">
            <li><Icon name="comment" title="comment" /></li>
            <li>Source</li>
          </ul>
          {sourceComment}
          <ul className="list-inline">
            <li><Icon name="comment" title="comment" /></li>
            <li>Target</li>
          </ul>
          {targetComment}
        </span>
      </Panel>
    )
  }
}

LocalProjectDetailPanel.propTypes = {
  /* Key of this panel within the group, used to expand/collapse */
  eventKey: PropTypes.number.isRequired,
  /* Source text for the match */
  source: PropTypes.string.isRequired,
  /* Translated text for the match */
  target: PropTypes.string.isRequired,
  /* Detailed descriptive and context information for the match. */
  matchDetail: PropTypes.shape({
    // This only renders things from local projects
    type: PropTypes.oneOf(['LOCAL_PROJECT']).isRequired,
    sourceComment: PropTypes.string,
    targetComment: PropTypes.string,
    projectId: PropTypes.string.isRequired,
    projectName: PropTypes.string.isRequired,
    version: PropTypes.string.isRequired,
    lastModifiedBy: PropTypes.string,
    lastModifiedDate: PropTypes.string.isRequired
  }).isRequired
}

export default LocalProjectDetailPanel
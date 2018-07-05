package com.kejis.requestutil.db;

/**
 * ClassName:	DownState
 * Function:	${TODO} 保存下载的状态
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/26 16:19
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public enum DownState {
    START(0),
    DOWN(1),
    PAUSE(2),
    STOP(3),
    ERROR(4),
    FINISH(5);
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    DownState(int state) {
        this.state = state;
    }
}

import {useAppSelector} from '@/redux/config/hooks';
import {signOut} from '@/redux/slides/authentication/authenticationSlide';
import {Navigate} from '@/utils/common';
import NGText from '@components/ng-text/NGText';
import BarChartIcon from '@mui/icons-material/BarChart';
import {Stack, SxProps, Tooltip} from '@mui/material';
import ListItemButton from '@mui/material/ListItemButton';
import {ReactNode} from 'react';
import {useDispatch} from 'react-redux';
import {NavLink, useLocation} from 'react-router-dom';

interface Type {
  logout?: boolean;
  goto: string;
  listName?: string;
  listIcon: ReactNode;
  myStyleText?: SxProps;
  title?: string;
}

function NGRoute({
  listIcon = <BarChartIcon />,
  goto,
  title,
  listName = '',
  logout = false,
  myStyleText,
}: Type) {
  const {theme} = useAppSelector(state => state.enterprise);
  const location = useLocation();
  const dispatch = useDispatch();
  return (
    <NavLink
      onClick={() => {
        logout && dispatch(signOut());
      }}
      to={Navigate(goto)}
      style={({isActive}) => {
        return {
          fontWeight: isActive ? 'bold' : '',
          borderRight:
            isActive && listName === ''
              ? `3px solid ${theme[0].mainColor}`
              : '',
          background: isActive && listName !== '' ? '#F3F5FD' : '',
          borderRadius: isActive && listName !== '' ? 4 : 0,
          height: isActive && listName !== '' ? 32 : 'auto',
          width: isActive && listName !== '' ? '243px' : 'auto',
          textDecoration: 'none',
        };
      }}>
      {listName === '' ? (
        <Tooltip title={title} placement="right-start">
          <ListItemButton>
            <NGText
              text={listIcon}
              fontSize={'12px'}
              myStyle={{
                color:
                  location.pathname === goto || location.pathname.includes(goto)
                    ? `${theme[0].mainColor}`
                    : '#333333',
              }}
            />
          </ListItemButton>
        </Tooltip>
      ) : (
        <Stack direction={'row'} spacing={1} alignItems={'center'} mx={2}>
          <NGText text={listIcon} />
          <NGText
            text={listName}
            myStyle={{textDecoration: 'none', ...myStyleText}}
            fontSize="12px"
            fontWeight="500"
          />
        </Stack>
      )}
    </NavLink>
  );
}

export default NGRoute;
